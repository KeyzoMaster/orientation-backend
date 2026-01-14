package sn.uidt.orientation.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Ajout Logger
import sn.uidt.orientation.constants.StatutResultat;
import sn.uidt.orientation.model.security.Utilisateur;
import sn.uidt.orientation.model.student.*;
import sn.uidt.orientation.repository.InscriptionAnnuelleRepository;
import sn.uidt.orientation.repository.UtilisateurRepository;

@Service
@RequiredArgsConstructor
@Slf4j // Ajout Logger
public class ExpertService {

    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    @Value("${prolog.rules-file-path}")
    private String rulesPath;

    public String analyserParcours(Long etudiantId) {
        List<InscriptionAnnuelle> parcoursActuel = inscriptionRepository.findByEtudiantIdOrderByAnneeAcademiqueAsc(etudiantId);
        
        if (parcoursActuel.isEmpty()) {
            return """
                {
                    "specialiteL3": "Données insuffisantes",
                    "probabiliteMasterPublic": 0.0,
                    "probabiliteMasterPrive": 0.0,
                    "matieresACorriger": [],
                    "conseilTrajectoire": "Aucune donnée académique trouvée."
                }
                """;
        }

        String faitsEtudiant = genererFaitsProlog(etudiantId, parcoursActuel, false);
        List<Utilisateur> anciens = utilisateurRepository.findByEstAncienTrue();
        String faitsAnciens = genererFaitsAnciens(anciens);

        String factsComplets = faitsAnciens + "\n" + faitsEtudiant;
        
        // DEBUG: Afficher les faits générés pour vérifier
        log.debug("--- FAITS PROLOG GENERES ---\n{}", factsComplets);
        
        return executerProlog(factsComplets, etudiantId);
    }
    
    public Long getEtudiantIdByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .map(Utilisateur::getId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé pour l'email : " + email));
    }

    private String genererFaitsAnciens(List<Utilisateur> anciens) {
        StringBuilder sb = new StringBuilder();
        // Directives ajoutées une seule fois ici de préférence
        sb.append(":- discontiguous est_ancien/1.\n");
        sb.append(":- discontiguous annee_academique/7.\n");
        sb.append(":- discontiguous statut_ue/5.\n");
        sb.append(":- discontiguous note_ec/5.\n");
        sb.append(":- style_check(-singleton).\n");
        
        for (Utilisateur ancien : anciens) {
            List<InscriptionAnnuelle> parcoursAncien = inscriptionRepository.findByEtudiantIdOrderByAnneeAcademiqueAsc(ancien.getId());
            sb.append(genererFaitsProlog(ancien.getId(), parcoursAncien, true));
        }
        return sb.toString();
    }

    private String genererFaitsProlog(Long userId, List<InscriptionAnnuelle> parcours, boolean estAncien) {
        StringBuilder sb = new StringBuilder();
        
        // Pour l'étudiant actuel, on ajoute aussi la directive est_ancien(false) ou juste rien
        if (estAncien) {
            sb.append(String.format("est_ancien(%d).\n", userId));
        }

        for (InscriptionAnnuelle insc : parcours) {
            String filiereCode = (insc.getSpecialite() != null && insc.getSpecialite().getFiliere() != null) 
                               ? insc.getSpecialite().getFiliere().getCode() : "unknown";
            String specCode = (insc.getSpecialite() != null) ? insc.getSpecialite().getCode() : "unknown";
            
            // Gestion safe des nulls pour éviter "null" dans Prolog
            String cycle = (insc.getCycle() != null) ? insc.getCycle().toLowerCase() : "licence";
            String decision = (insc.getDecisionConseil() != null) ? insc.getDecisionConseil().toLowerCase().replace(" ", "_") : "unknown";

            sb.append(String.format("annee_academique(%d, %d, '%s', '%s', '%s', '%s', '%s').\n",
                    userId,
                    insc.getAnneeAcademique(),
                    filiereCode.toLowerCase(),
                    "annuel", 
                    cycle,
                    specCode.toLowerCase(),
                    decision
            ));

            if (insc.getInscriptionsSemestrielles() != null) {
                for (InscriptionSemestrielle is : insc.getInscriptionsSemestrielles()) {
                    if (is.getResultatsUE() != null) {
                        for (ResultatUE resUE : is.getResultatsUE()) {
                            boolean validee = (resUE.getStatut() == StatutResultat.VALIDE 
                                            || resUE.getStatut() == StatutResultat.COMPENSE 
                                            || resUE.getStatut() == StatutResultat.ACQUIS_ANTERIEUR);
                            boolean premiereFois = !resUE.isReportDeNote();

                            // Attention aux single quotes dans les codes UE (rare mais possible)
                            String codeUE = resUE.getUe().getCode().toLowerCase().replace("'", "");

                            sb.append(String.format(Locale.US, "statut_ue(%d, '%s', %d, %b, %b).\n",
                                    userId, codeUE, insc.getAnneeAcademique(), 
                                    premiereFois, validee));

                            if (resUE.getNotesEC() != null) {
                                for (NoteEC note : resUE.getNotesEC()) {
                                    sb.append(String.format(Locale.US, "note_ec(%d, '%s', %.2f, '%s', %d).\n",
                                            userId, 
                                            codeUE,
                                            note.getNote(), 
                                            note.getSession().name().toLowerCase(), 
                                            insc.getAnneeAcademique()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    private String executerProlog(String facts, Long etudiantId) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("facts_" + etudiantId, ".pl");
            Files.writeString(tempFile, facts);

            // Vérification existence fichier règles
            if (rulesPath == null || !Files.exists(Path.of(rulesPath))) {
                log.error("Fichier de règles introuvable : " + rulesPath);
                return "{\"error\": \"Configuration Prolog invalide (Règles introuvables)\"}";
            }

            ProcessBuilder pb = new ProcessBuilder(
                    "swipl", "-q", 
                    "-s", rulesPath, 
                    "-s", tempFile.toString(),
                    "-g", "consultation_json(" + etudiantId + ")",
                    "-t", "halt"
            );
            
            pb.redirectErrorStream(true);

            Process process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                // MODIFICATION : On capture TOUT pour voir les erreurs
                String output = reader.lines().collect(Collectors.joining("\n"));

                log.info("Sortie Prolog brute : \n" + output);

                int jsonStart = output.indexOf("{");
                int jsonEnd = output.lastIndexOf("}");
                
                if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                    return output.substring(jsonStart, jsonEnd + 1);
                } else {
                    // Ici on retourne l'erreur brute pour que vous la voyiez dans le Postman/Console
                    log.error("Echec Prolog, sortie : " + output);
                    return "{\"error\": \"Erreur Prolog : " + output.replace("\"", "'").replace("\n", " ") + "\"}";
                }
            }

        } catch (IOException e) {
            log.error("Erreur IO Prolog", e);
            return "{\"error\": \"Erreur technique: " + e.getMessage() + "\"}";
        } finally {
            try {
                if (tempFile != null) Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {}
        }
    }
}