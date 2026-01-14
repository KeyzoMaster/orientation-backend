package sn.uidt.orientation.service;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.uidt.orientation.constants.Role;
import sn.uidt.orientation.constants.Semestre;
import sn.uidt.orientation.constants.StatutResultat;
import sn.uidt.orientation.constants.TypeInscriptionUE;
import sn.uidt.orientation.constants.TypeSession;
import sn.uidt.orientation.model.maquette.*;
import sn.uidt.orientation.model.security.Utilisateur;
import sn.uidt.orientation.model.student.*;
import sn.uidt.orientation.repository.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeederService {

    private final UtilisateurRepository utilisateurRepository;
    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final ResultatUERepository resultatUERepository;
    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final CandidatureMasterRepository candidatureMasterRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminPedagogiqueService adminService;

    private final Random random = new Random();

    private enum ProfilEtudiant {
        EXCELLENT(16, 1.5),
        BON(13.5, 1.5),
        MOYEN(11, 2.0),
        DIFFICILE(9.0, 2.5);

        final double moyenneCible;
        final double ecartType;

        ProfilEtudiant(double m, double e) { this.moyenneCible = m; this.ecartType = e; }
    }

    @Transactional
    public void seederAnciensEtudiants(int nombre) {
        // ID unique pour ce lot (Timestamp actuel)
        long batchId = System.currentTimeMillis();

        log.info("==================================================================");
        log.info("START SEEDING: Génération de {} nouveaux anciens (Batch {})", nombre, batchId);
        log.info("==================================================================");

        Specialite l1Tc = specialiteRepository.findByCode("L_INFO").orElseThrow(); 
        Specialite l3Gl = specialiteRepository.findByCode("L3_GL").orElseThrow();
        Specialite l3Rt = specialiteRepository.findByCode("L3_RT").orElseThrow();
        Specialite m1Gl = specialiteRepository.findByCode("M_GL").orElse(null); 
        Specialite m1Rt = specialiteRepository.findByCode("M_RT").orElse(null);
        Filiere filiereInfo = filiereRepository.findByCode("INFO").orElseThrow();

        int reussites = 0;
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= nombre; i++) {
            try {
                // On passe le batchId pour garantir l'unicité
                boolean succes = genererParcoursAncien(i, batchId, filiereInfo, l1Tc, l3Gl, l3Rt, m1Gl, m1Rt);
                if (succes) reussites++;
                
                if (i % 50 == 0) {
                    log.info(">>> Progression: {}/{} étudiants traités...", i, nombre);
                }
            } catch (Exception e) {
                log.error("Erreur seeding ancien #{}: {}", i, e.getMessage());
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("==================================================================");
        log.info("SEEDING TERMINÉ en {} ms. {} parcours complets générés.", duration, reussites);
        log.info("==================================================================");
    }

    private boolean genererParcoursAncien(int index, long batchId, Filiere filiere, Specialite l1, Specialite l3Gl, Specialite l3Rt,
                                       Specialite mGl, Specialite mRt) {
        
        // CORRECTION : Concaténation Compteur + Timestamp (BatchId)
        String email = "ancien_" + index + "_" + batchId + "@archive.univ-thies.sn";
        
        // Double sécurité (normalement inutile avec le timestamp)
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            log.debug("Skipping: {} existe déjà.", email);
            return false;
        }

        ProfilEtudiant profil = choisirProfilAleatoire();
        int anneeCourante = 2017 + random.nextInt(3);

        Utilisateur etudiant = new Utilisateur();
        // Le Code Anonyme inclut aussi une partie du timestamp pour éviter les doublons en base
        // Format : ANC-ANNEE-BATCH_SUFFIX-INDEX (ex: ANC-2018-999-1)
        etudiant.setCodeAnonyme(String.format("ANC-%d-%d-%04d", anneeCourante, (batchId % 1000), index));
        etudiant.setEmail(email);
        etudiant.setPassword(passwordEncoder.encode("pass"));
        etudiant.setRole(Role.ETUDIANT);
        etudiant.setEstAncien(true);
        utilisateurRepository.save(etudiant);

        log.trace("Génération parcours pour {} (Profil: {})", email, profil);

        // --- L1 ---
        if (!validerCycle(etudiant, l1, profil, anneeCourante, 3)) return false; 
        anneeCourante = getLastAnnee(etudiant) + 1;

        // --- L2 ---
        if (!validerCycle(etudiant, l1, profil, anneeCourante, 3)) return false;
        anneeCourante = getLastAnnee(etudiant) + 1;

        // --- L3 ---
        Specialite speL3 = random.nextBoolean() ? l3Gl : l3Rt;
        if (!validerCycle(etudiant, speL3, profil, anneeCourante, 2)) return false;
        anneeCourante = getLastAnnee(etudiant) + 1;

        // --- MASTER ---
        if (mGl != null && mRt != null) {
            Specialite masterVise = (speL3.getCode().equals("L3_GL")) ? mGl : mRt;
            double moyL3 = getLastMoyenne(etudiant);
            simulerCandidatureEtMaster(etudiant, anneeCourante, masterVise, profil, moyL3);
        }
        return true;
    }

    @Transactional
    public void seederParcoursEtudiant(String email) {
        log.info("--- Simulation Parcours Actuel: {} ---", email);
        Utilisateur etudiant = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        if (!inscriptionRepository.findByEtudiantIdOrderByAnneeAcademiqueAsc(etudiant.getId()).isEmpty()) {
            log.warn("Parcours déjà existant pour {}, abandon.", email);
            return;
        }

        Specialite l1Tc = specialiteRepository.findByCode("L_INFO").orElseThrow();
        Specialite l3Gl = specialiteRepository.findByCode("L3_GL").orElseThrow();
        Specialite l3Rt = specialiteRepository.findByCode("L3_RT").orElseThrow();

        ProfilEtudiant profil = choisirProfilAleatoire();
        int anneeCourante = 2021; 

        // Simulation L1
        if (!simulerAnnee(etudiant, anneeCourante, l1Tc, profil)) {
            log.info("L1: Redoublement en 2021");
            anneeCourante++;
            simulerAnnee(etudiant, anneeCourante, l1Tc, ProfilEtudiant.MOYEN); 
        } else {
            log.info("L1: Validée du premier coup");
        }
        anneeCourante++;

        // Simulation L2
        if (!simulerAnnee(etudiant, anneeCourante, l1Tc, profil)) {
             log.info("L2: Redoublement en {}", anneeCourante);
             anneeCourante++;
             simulerAnnee(etudiant, anneeCourante, l1Tc, ProfilEtudiant.MOYEN);
        } else {
            log.info("L2: Validée");
        }
        anneeCourante++;

        // Inscription L3 (En cours)
        Specialite speL3 = random.nextBoolean() ? l3Gl : l3Rt;
        adminService.inscrireEtudiant(etudiant.getId(), anneeCourante, speL3);

        log.info(">>> Parcours généré avec succès. Inscrit en {} pour l'année {}.", speL3.getLibelle(), anneeCourante);
    }

    private boolean validerCycle(Utilisateur etudiant, Specialite spec, ProfilEtudiant profil, int anneeDepart, int maxEssais) {
        int essais = 0;
        boolean valide = false;
        int anneeActuelle = anneeDepart;
        ProfilEtudiant profilCourant = profil;

        while (!valide && essais < maxEssais) {
            valide = simulerAnnee(etudiant, anneeActuelle, spec, profilCourant);
            anneeActuelle++;
            essais++;
            // if (!valide && profilCourant == ProfilEtudiant.DIFFICILE) profilCourant = ProfilEtudiant.MOYEN;
        }
        return valide;
    }

    /**
     * Simule une année avec calcul par SEMESTRE
     */
    private boolean simulerAnnee(Utilisateur etudiant, int annee, Specialite spec, ProfilEtudiant profil) {
        InscriptionAnnuelle inscription = adminService.inscrireEtudiant(etudiant.getId(), annee, spec);
        recupererDettesAnterieures(etudiant, inscription);

        double sommeMoyennesSemestres = 0;
        int totalCreditsAnnee = 0;
        int nbSemestres = inscription.getInscriptionsSemestrielles().size();

        for (InscriptionSemestrielle is : inscription.getInscriptionsSemestrielles()) {
            double sommeMoyenneUE = 0;
            int countUE = 0;
            int creditsSemestre = 0;
            
            List<ResultatUE> resultatsATraiter = new ArrayList<>(is.getResultatsUE());

            for (ResultatUE res : resultatsATraiter) {
                double moyUE = genererNotesPourUE(inscription.getId(), res, profil);
                sommeMoyenneUE += moyUE;
                countUE++;
                
                if (moyUE >= 10 || res.isReportDeNote()) {
                    creditsSemestre += res.getUe().getCredits();
                }
            }
            
            double moySemestre = (countUE > 0) ? sommeMoyenneUE / countUE : 0;
            is.setMoyenneSemestre(moySemestre);
            is.setCreditsObtenus(creditsSemestre);
            is.setEstValide(moySemestre >= 10);
            
            sommeMoyennesSemestres += moySemestre;
            totalCreditsAnnee += creditsSemestre;
        }

        double moyAnnuelle = (nbSemestres > 0) ? sommeMoyennesSemestres / nbSemestres : 0;
        inscription.setMoyenneAnnuelle(moyAnnuelle);

        boolean passage = false;
        if (moyAnnuelle >= 10.0) {
            inscription.setDecisionConseil("ADMIS");
            passage = true;
        } else if (totalCreditsAnnee >= 42) { // AJAC
            inscription.setDecisionConseil("PASSAGE_CONDITIONNEL");
            passage = true; 
        } else {
            inscription.setDecisionConseil("REDOUBLANT");
            passage = false;
        }
        
        inscriptionRepository.save(inscription);
        log.trace("Année {}: Moyenne={}, Décision={}", annee, String.format("%.2f", moyAnnuelle), inscription.getDecisionConseil());
        return passage;
    }

    private void recupererDettesAnterieures(Utilisateur etudiant, InscriptionAnnuelle inscriptionActuelle) {
        List<InscriptionAnnuelle> historique = inscriptionRepository.findByEtudiantIdOrderByAnneeAcademiqueAsc(etudiant.getId());
        
        for (InscriptionAnnuelle ancienneInsc : historique) {
            if (ancienneInsc.getId().equals(inscriptionActuelle.getId())) continue;

            for (InscriptionSemestrielle ancienSem : ancienneInsc.getInscriptionsSemestrielles()) {
                for (ResultatUE ancienRes : ancienSem.getResultatsUE()) {
                    if (ancienRes.getMoyenne() != null && ancienRes.getMoyenne() < 10.0) {
                        
                        Semestre semestreDette = ancienRes.getUe().getMaquetteSemestre().getSemestre();
                        boolean estImpair = semestreDette.name().endsWith("1") || semestreDette.name().endsWith("3") || semestreDette.name().endsWith("5");
                        
                        InscriptionSemestrielle targetSemestre = inscriptionActuelle.getInscriptionsSemestrielles().stream()
                            .filter(is -> {
                                boolean currentImpair = is.getSemestre().name().endsWith("1") || is.getSemestre().name().endsWith("3") || is.getSemestre().name().endsWith("5");
                                return currentImpair == estImpair;
                            })
                            .findFirst()
                            .orElse(inscriptionActuelle.getInscriptionsSemestrielles().get(0));

                        boolean dejaPresente = targetSemestre.getResultatsUE().stream()
                            .anyMatch(r -> r.getUe().getCode().equals(ancienRes.getUe().getCode()));

                        if (!dejaPresente) {
                            ResultatUE dette = new ResultatUE();
                            dette.setUe(ancienRes.getUe());
                            dette.setInscriptionSemestrielle(targetSemestre);
                            dette.setStatut(StatutResultat.EN_COURS);
                            dette.setReportDeNote(false);
                            dette.setTypeInscription(TypeInscriptionUE.DETTE);
                            dette = resultatUERepository.save(dette);
                            targetSemestre.getResultatsUE().add(dette);
                        }
                    }
                }
            }
        }
    }

    private double genererNotesPourUE(Long inscriptionAnnuelleId, ResultatUE res, ProfilEtudiant profil) {
        if (res.isReportDeNote()) {
            return res.getMoyenne() != null ? res.getMoyenne() : 10.0;
        }

        List<EC> ecs = res.getUe().getEcs();
        if (ecs == null || ecs.isEmpty()) return 10.0;

        Map<EC, Double> notesNormales = new HashMap<>();
        double sommeNormale = 0;

        for (EC ec : ecs) {
            double noteVal = genererNoteGaussienne(profil);
            notesNormales.put(ec, noteVal);
            sommeNormale += noteVal;
            adminService.saisirNote(inscriptionAnnuelleId, res.getUe().getCode(), ec.getLibelle(), noteVal, TypeSession.NORMALE);
        }

        double moyenneNormaleUE = sommeNormale / ecs.size();
        double sommeFinale = 0;

        for (EC ec : ecs) {
            double noteNormale = notesNormales.get(ec);
            double noteFinale = noteNormale;

            boolean rattrapageRequis = (moyenneNormaleUE < 10.0 && noteNormale < 10.0) || (noteNormale < 7.0);

            if (rattrapageRequis) {
                double boost = (profil == ProfilEtudiant.DIFFICILE) ? 1.0 : 3.5;
                double noteRattrapage = Math.min(20, noteNormale + random.nextDouble() * boost);
                adminService.saisirNote(inscriptionAnnuelleId, res.getUe().getCode(), ec.getLibelle(), noteRattrapage, TypeSession.RATTRAPAGE);
                noteFinale = Math.max(noteNormale, noteRattrapage);
            }
            sommeFinale += noteFinale;
        }

        return sommeFinale / ecs.size();
    }

    // --- METHODES MASTER ---
    
    private void simulerCandidatureEtMaster(Utilisateur etudiant, int annee, Specialite masterVise, 
                                            ProfilEtudiant profil, double moyenneL3) {
        InscriptionAnnuelle derniereInsc = inscriptionRepository
                .findByEtudiantIdOrderByAnneeAcademiqueAsc(etudiant.getId()).getLast();
        
        String codeL3 = derniereInsc.getSpecialite().getCode();
        String codeMaster = masterVise.getCode();
        String suffixeL3 = codeL3.substring(codeL3.lastIndexOf('_') + 1);
        String suffixeMaster = codeMaster.substring(codeMaster.lastIndexOf('_') + 1);

        if (!suffixeL3.equals(suffixeMaster)) return;

        boolean admis = false;
        String typeFormation = "PUBLIQUE";
        
        double chancePublic = (profil == ProfilEtudiant.EXCELLENT) ? 1.0 : (profil == ProfilEtudiant.BON ? 0.75 : 0.2);
        if (moyenneL3 > 14) chancePublic += 0.2;

        if (random.nextDouble() < chancePublic) admis = true;
        else if (profil != ProfilEtudiant.DIFFICILE && moyenneL3 >= 10) {
             double chancePrive = (profil == ProfilEtudiant.MOYEN) ? 0.8 : 0.95;
             if (random.nextDouble() < chancePrive) { admis = true; typeFormation = "PRIVEE"; }
        }

        CandidatureMaster cm = new CandidatureMaster();
        cm.setEtudiant(etudiant); cm.setSpecialite(masterVise); 
        cm.setTypeFormation(typeFormation); cm.setVerdict(admis ? "ACCEPTE" : "REJETE");
        candidatureMasterRepository.save(cm);
        
        log.trace("Candidature Master {} ({}): {}", masterVise.getCode(), typeFormation, cm.getVerdict());

        if (admis) simulerCursusMaster(etudiant, annee, masterVise, profil);
    }

    private void simulerCursusMaster(Utilisateur etudiant, int annee, Specialite master, ProfilEtudiant profil) {
        boolean m1 = simulerAnnee(etudiant, annee, master, profil);
        if (!m1) {
            annee++;
            m1 = simulerAnnee(etudiant, annee, master, ProfilEtudiant.MOYEN);
        }
        if (!m1) return;

        annee++;
        boolean m2 = simulerAnnee(etudiant, annee, master, profil);
        if (!m2) {
            annee++;
            simulerAnnee(etudiant, annee, master, profil);
        }
    }

    private double genererNoteGaussienne(ProfilEtudiant profil) {
        double val = random.nextGaussian() * profil.ecartType + profil.moyenneCible;
        val += (random.nextDouble() - 0.5); 
        return Math.max(0, Math.min(20, Math.round(val * 2) / 2.0));
    }

    private ProfilEtudiant choisirProfilAleatoire() {
        int r = random.nextInt(100);
        if (r < 15) return ProfilEtudiant.EXCELLENT;
        if (r < 45) return ProfilEtudiant.BON;
        if (r < 80) return ProfilEtudiant.MOYEN;
        return ProfilEtudiant.DIFFICILE;
    }

    private int getLastAnnee(Utilisateur etudiant) {
        return inscriptionRepository.findByEtudiantIdOrderByAnneeAcademiqueAsc(etudiant.getId())
                .getLast().getAnneeAcademique();
    }
    
    private double getLastMoyenne(Utilisateur etudiant) {
         return inscriptionRepository.findByEtudiantIdOrderByAnneeAcademiqueAsc(etudiant.getId())
                .getLast().getMoyenneAnnuelle();
    }
}