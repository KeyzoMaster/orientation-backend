package sn.uidt.orientation.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.model.security.Utilisateur;
import sn.uidt.orientation.model.student.InscriptionAnnuelle;
import sn.uidt.orientation.model.student.NoteEC;
import sn.uidt.orientation.model.student.ResultatUE;
import sn.uidt.orientation.repository.InscriptionAnnuelleRepository;
import sn.uidt.orientation.repository.UtilisateurRepository;

@Service
@RequiredArgsConstructor
public class ExpertService {

    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    @Value("${prolog.rules-file-path}")
    private String rulesPath;

    /**
     * Analyse le dossier pour l'orientation L3 ou Master
     */
    public String analyserParcours(Long etudiantId) {
        List<InscriptionAnnuelle> parcours = inscriptionRepository.findByEtudiantIdOrderByAnneeAsc(etudiantId);
        
        // CORRECT : On renvoie une structure JSON compatible avec RecommandationDto
        if (parcours.isEmpty()) {
            return """
                {
                    "specialiteL3": "Données insuffisantes",
                    "probabiliteMasterPublic": 0.0,
                    "probabiliteMasterPrive": 0.0,
                    "matieresACorriger": [],
                    "conseilTrajectoire": "Aucune donnée académique trouvée. Veuillez saisir vos notes pour obtenir une simulation."
                }
                """;
        }

        // 1. Traduction du parcours en faits Prolog
        String facts = genererFaitsProlog(etudiantId, parcours);

        // 2. Exécution du moteur Prolog
        return executerProlog(facts, etudiantId);
    }

    private String genererFaitsProlog(Long etudiantId, List<InscriptionAnnuelle> parcours) {
        StringBuilder sb = new StringBuilder();

        for (InscriptionAnnuelle insc : parcours) {
            // Fait: annee_academique(EtudiantId, Annee, Filiere, Niveau, Cycle, Specialite, Decision)
            sb.append(String.format("annee_academique(%d, %d, '%s', '%s', '%s', '%s', '%s').\n",
                    etudiantId,
                    insc.getAnnee(),
                    insc.getSpecialite().getFiliere().getCode().toLowerCase(),
                    insc.getNiveau().toLowerCase(),
                    insc.getCycle().toLowerCase(),
                    insc.getSpecialite().getCode().toLowerCase(),
                    insc.getDecisionConseil().toLowerCase().replace(" ", "_")
            ));

            for (ResultatUE resUE : insc.getResultatsUE()) {
                // Fait: statut_ue(EtudiantId, CodeUE, Annee, PremiereFois, Validee)
                sb.append(String.format("statut_ue(%d, '%s', %d, %b, %b).\n",
                        etudiantId, resUE.getUe().getCode(), insc.getAnnee(), 
                        resUE.isPremiereFois(), resUE.isValidee()));

                for (NoteEC note : resUE.getNotesEC()) {
                    // Fait: note_ec(EtudiantId, CodeEC, Valeur, Session, Annee)
                    sb.append(String.format("note_ec(%d, '%s', %s, %s, %d).\n",
                            etudiantId, 
                            note.getEc().getLibelle().replace(" ", "_"), 
                            note.getNote(), 
                            note.getSession().toLowerCase(), 
                            insc.getAnnee()));
                }
            }
        }
        return sb.toString();
    }
    
    public Long getEtudiantIdByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .map(Utilisateur::getId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé pour l'email : " + email));
    }

    private String executerProlog(String facts, Long etudiantId) {
        try {
            // Créer un fichier temporaire pour les faits de cette session
            Path tempFile = Files.createTempFile("facts_etudiant_" + etudiantId, ".pl");
            Files.writeString(tempFile, facts);

            // Appel de SWI-Prolog
            // Commande: swipl -s regles.pl -s facts.pl -g "predicat_de_sortie, halt."
            ProcessBuilder pb = new ProcessBuilder(
                    "swipl", "-q", "-s", rulesPath, "-s", tempFile.toString(),
                    "-g", "consultation_json(" + etudiantId + "), halt."
            );

            Process process = pb.start();
            
            // Lecture du flux de sortie (Prolog doit renvoyer du JSON)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining());
            
            // Nettoyage
            Files.deleteIfExists(tempFile);
            
            return output;
        } catch (IOException e) {
            return "{\"error\": \"Erreur lors de l'appel au moteur Prolog: " + e.getMessage() + "\"}";
        }
    }
}