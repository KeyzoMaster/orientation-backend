package sn.uidt.orientation.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.uidt.orientation.model.student.InscriptionAnnuelle;

public interface InscriptionAnnuelleRepository extends JpaRepository<InscriptionAnnuelle, Long> {
    
    // Récupère tout le parcours d'un étudiant trié
    List<InscriptionAnnuelle> findByEtudiantIdOrderByAnneeAcademiqueAsc(Long etudiantId);

    // Trouver l'inscription d'un étudiant pour une année spécifique (ex: 2024)
    Optional<InscriptionAnnuelle> findByEtudiantIdAndAnneeAcademique(Long etudiantId, int anneeAcademique);
    
    // Trouver les inscriptions actives (si vous gérez un statut "CLOTURE" ou non)
    // Ici on suppose que c'est la dernière année max
    Optional<InscriptionAnnuelle> findTopByEtudiantIdOrderByAnneeAcademiqueDesc(Long etudiantId);
}