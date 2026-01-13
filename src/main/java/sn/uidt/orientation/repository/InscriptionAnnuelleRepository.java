package sn.uidt.orientation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.student.InscriptionAnnuelle;

public interface InscriptionAnnuelleRepository extends JpaRepository<InscriptionAnnuelle, Long> {
    // Récupère tout le parcours d'un étudiant (L1, L2, L3...) trié par année
    List<InscriptionAnnuelle> findByEtudiantIdOrderByAnneeAsc(Long etudiantId);
}