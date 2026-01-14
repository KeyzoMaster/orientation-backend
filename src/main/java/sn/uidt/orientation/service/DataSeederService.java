package sn.uidt.orientation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn.uidt.orientation.model.maquette.Filiere;
import sn.uidt.orientation.model.maquette.Specialite;
import sn.uidt.orientation.repository.FiliereRepository;
import sn.uidt.orientation.repository.InscriptionAnnuelleRepository;
import sn.uidt.orientation.repository.SpecialiteRepository;
import sn.uidt.orientation.repository.UtilisateurRepository;
import sn.uidt.orientation.model.security.Utilisateur;
import sn.uidt.orientation.service.EtudiantSimulationService.ProfilEtudiant;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeederService {

    private final EtudiantSimulationService simulationService;
    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final AdminPedagogiqueService adminService;

    // Nombre de threads (Ajuster selon CPU)
    private static final int THREAD_POOL_SIZE = 8;

    public void seederAnciensEtudiants(int nombre) {
        long batchId = System.currentTimeMillis();
        log.info("=== START SEEDING: {} étudiants (Batch {}) ===", nombre, batchId);
        log.info("Mode Multithread activé (Pool: {})", THREAD_POOL_SIZE);

        Specialite l1Tc = specialiteRepository.findByCode("L_INFO").orElseThrow(); 
        Specialite l3Gl = specialiteRepository.findByCode("L3_GL").orElseThrow();
        Specialite l3Rt = specialiteRepository.findByCode("L3_RT").orElseThrow();
        Specialite m1Gl = specialiteRepository.findByCode("M_GL").orElse(null); 
        Specialite m1Rt = specialiteRepository.findByCode("M_RT").orElse(null);
        Filiere filiereInfo = filiereRepository.findByCode("INFO").orElseThrow();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= nombre; i++) {
            final int index = i;
            // Tâche asynchrone
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return simulationService.genererParcoursAncien(
                        index, batchId, filiereInfo, l1Tc, l3Gl, l3Rt, m1Gl, m1Rt
                    );
                } catch (Exception e) {
                    log.error("Erreur async étudiant #{}: {}", index, e.getMessage());
                    return false;
                }
            }, executor);

            futures.add(future);
            if (i % 100 == 0) log.info(">>> Tâches soumises: {}/{}", i, nombre);
        }

        // Attente de la fin
        long successCount = futures.stream()
            .map(CompletableFuture::join)
            .filter(result -> result)
            .count();

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("=== SEEDING TERMINÉ en {} ms. {} réussites sur {}. ===", duration, successCount, nombre);
    }

    /**
     * Simulation d'un étudiant actuel (Logique préservée à 100%)
     */
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

        ProfilEtudiant profil = simulationService.choisirProfilAleatoire();
        int anneeCourante = 2021; 

        // Simulation L1 (Échec forcé puis Réussite - Logique Origine)
        if (!simulationService.simulerAnnee(etudiant, anneeCourante, l1Tc, profil)) {
            log.info("L1: Redoublement en 2021 (Simulé)");
            anneeCourante++;
            // On retente avec un profil MOYEN pour assurer le passage (Logique Origine)
            simulationService.simulerAnnee(etudiant, anneeCourante, l1Tc, ProfilEtudiant.MOYEN); 
        } else {
            log.info("L1: Validée du premier coup");
        }
        anneeCourante++;

        // Simulation L2 (Échec forcé puis Réussite - Logique Origine)
        if (!simulationService.simulerAnnee(etudiant, anneeCourante, l1Tc, profil)) {
             log.info("L2: Redoublement en {} (Simulé)", anneeCourante);
             anneeCourante++;
             simulationService.simulerAnnee(etudiant, anneeCourante, l1Tc, ProfilEtudiant.MOYEN);
        } else {
            log.info("L2: Validée");
        }
        anneeCourante++;

        // Inscription L3 (En cours)
        //Specialite speL3 = ThreadLocalRandom.current().nextBoolean() ? l3Gl : l3Rt;
        //adminService.inscrireEtudiant(etudiant.getId(), anneeCourante, speL3);

        // log.info(">>> Parcours généré avec succès. Inscrit en {} pour l'année {}.", speL3.getLibelle(), anneeCourante);
    }
}