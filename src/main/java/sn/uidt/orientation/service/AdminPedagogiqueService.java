package sn.uidt.orientation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.constants.StatutResultat;
import sn.uidt.orientation.constants.TypeInscriptionUE;
import sn.uidt.orientation.constants.TypeSession;
import sn.uidt.orientation.model.maquette.*;
import sn.uidt.orientation.model.security.Utilisateur;
import sn.uidt.orientation.model.student.*;
import sn.uidt.orientation.repository.*;

@Service
@RequiredArgsConstructor
public class AdminPedagogiqueService {

    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final ResultatUERepository resultatUERepository;
    private final NoteECRepository noteECRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional
    public InscriptionAnnuelle inscrireEtudiant(Long etudiantId, int annee, Specialite specialite) {
        Utilisateur etudiant = utilisateurRepository.findById(etudiantId).orElseThrow();

        InscriptionAnnuelle inscription = new InscriptionAnnuelle();
        inscription.setEtudiant(etudiant);
        inscription.setAnneeAcademique(annee);
        inscription.setSpecialite(specialite);
        
        // Déduction automatique du cycle (LICENCE/MASTER) pour le système expert
        if (specialite.getCode().startsWith("M") || specialite.getLibelle().contains("Master")) {
            inscription.setCycle("MASTER");
        } else {
            inscription.setCycle("LICENCE");
        }

        inscription.setInscriptionsSemestrielles(new ArrayList<>());
        
        inscription = inscriptionRepository.save(inscription);

        List<MaquetteSemestre> maquettes = specialite.getMaquettes();

        for (MaquetteSemestre ms : maquettes) {
            InscriptionSemestrielle inscSem = new InscriptionSemestrielle();
            inscSem.setSemestre(ms.getSemestre());
            inscSem.setInscriptionAnnuelle(inscription);
            inscSem.setResultatsUE(new ArrayList<>());
            
            for (UE ue : ms.getUes()) {
                creerInscriptionPedagogique(inscSem, ue, etudiantId);
            }
            
            inscription.getInscriptionsSemestrielles().add(inscSem);
        }

        return inscriptionRepository.save(inscription);
    }

    private void creerInscriptionPedagogique(InscriptionSemestrielle inscSem, UE ue, Long etudiantId) {
        // CORRECTION : On récupère une liste pour gérer les doublons historiques (ex: redoublements successifs)
        // Nécessite que ResultatUERepository retourne List<ResultatUE> et non Optional
        List<ResultatUE> dejaValidees = resultatUERepository.findDejaValidee(etudiantId, ue.getCode());

        ResultatUE res = new ResultatUE();
        res.setUe(ue);
        res.setInscriptionSemestrielle(inscSem);
        
        if (!dejaValidees.isEmpty()) {
            // On prend le premier résultat valide trouvé (le plus ancien ou le premier de la liste)
            ResultatUE validationPrecedente = dejaValidees.get(0);
            
            res.setStatut(StatutResultat.ACQUIS_ANTERIEUR);
            res.setMoyenne(validationPrecedente.getMoyenne());
            res.setReportDeNote(true);
            res.setTypeInscription(TypeInscriptionUE.STANDARD); 
        } else {
            res.setStatut(StatutResultat.EN_COURS);
            res.setReportDeNote(false);
            res.setTypeInscription(TypeInscriptionUE.STANDARD);
        }
        inscSem.getResultatsUE().add(res);
    }
    
    @Transactional
    public NoteEC saisirNote(Long inscriptionAnnuelleId, String codeUE, String nomEC, double valeur, TypeSession session) {
        var inscAnnuelle = inscriptionRepository.findById(inscriptionAnnuelleId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable ID: " + inscriptionAnnuelleId));

        // 1. Recherche du ResultatUE dans les semestres de l'année
        ResultatUE tempResUE = null;
        
        for (InscriptionSemestrielle is : inscAnnuelle.getInscriptionsSemestrielles()) {
            Optional<ResultatUE> optRes = is.getResultatsUE().stream()
                .filter(r -> r.getUe().getCode().equals(codeUE))
                .findFirst();
            
            if (optRes.isPresent()) {
                tempResUE = optRes.get();
                break;
            }
        }
        
        if (tempResUE == null) {
            throw new RuntimeException("L'étudiant n'est pas inscrit à l'UE " + codeUE + " pour l'année " + inscAnnuelle.getAnneeAcademique());
        }
        
        if (tempResUE.isReportDeNote()) {
            throw new RuntimeException("L'UE " + codeUE + " est déjà acquise par capitalisation.");
        }

        // 2. Assignation Finale (Pour utilisation dans le lambda)
        final ResultatUE resUE = tempResUE;

        var ecCible = resUE.getUe().getEcs().stream()
                .filter(ec -> ec.getLibelle().equalsIgnoreCase(nomEC))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("EC introuvable : " + nomEC));

        if (resUE.getNotesEC() == null) {
            resUE.setNotesEC(new ArrayList<>());
        }

        resUE.getNotesEC().stream()
                .filter(n -> n.getEc().equals(ecCible) && n.getSession() == session)
                .findFirst()
                .ifPresentOrElse(
                    noteExistante -> noteExistante.setNote(valeur),
                    () -> {
                        var nouvelleNote = new NoteEC();
                        nouvelleNote.setEc(ecCible);
                        nouvelleNote.setResultatUE(resUE);
                        nouvelleNote.setSession(session);
                        nouvelleNote.setNote(valeur);
                        resUE.getNotesEC().add(nouvelleNote);
                    }
                );

        var noteAFichier = resUE.getNotesEC().stream()
                .filter(n -> n.getEc().equals(ecCible) && n.getSession() == session)
                .findFirst()
                .orElseThrow();

        var savedNote = noteECRepository.save(noteAFichier);
        calculerMoyenneUE(resUE);

        return savedNote;
    }

    private void calculerMoyenneUE(ResultatUE resUE) {
        List<NoteEC> notes = noteECRepository.findByResultatUEId(resUE.getId());
        if (notes.isEmpty()) return;

        Map<Long, Double> meilleuresNotesParEC = new HashMap<>();

        for (NoteEC n : notes) {
            Long ecId = n.getEc().getId();
            double noteActuelle = n.getNote();
            
            if (meilleuresNotesParEC.containsKey(ecId)) {
                double noteExistante = meilleuresNotesParEC.get(ecId);
                meilleuresNotesParEC.put(ecId, Math.max(noteExistante, noteActuelle));
            } else {
                meilleuresNotesParEC.put(ecId, noteActuelle);
            }
        }

        double somme = 0;
        for (Double val : meilleuresNotesParEC.values()) {
            somme += val;
        }

        int nombreEC = resUE.getUe().getEcs().size(); 
        
        if (nombreEC > 0) {
            double moyenne = somme / nombreEC;
            resUE.setMoyenne(moyenne);
            
            if (moyenne >= 10) {
                resUE.setStatut(StatutResultat.VALIDE);
            } else {
                if (resUE.getStatut() != StatutResultat.COMPENSE) { 
                     resUE.setStatut(StatutResultat.AJOURNE);
                }
            }
            resultatUERepository.save(resUE);
        }
    }

    @Transactional
    public void ajouterDossierAncien(Utilisateur ancien, List<InscriptionAnnuelle> historique) {
        ancien.setEstAncien(true);
        utilisateurRepository.save(ancien);
        for (InscriptionAnnuelle ia : historique) {
            ia.setEtudiant(ancien);
            inscriptionRepository.save(ia);
        }
    }
}