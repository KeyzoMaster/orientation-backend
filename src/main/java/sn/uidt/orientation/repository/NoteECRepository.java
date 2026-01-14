package sn.uidt.orientation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.uidt.orientation.model.student.NoteEC;

public interface NoteECRepository extends JpaRepository<NoteEC, Long> {
    List<NoteEC> findByResultatUEId(Long resultatUEId);
    
    // Trouver toutes les notes d'une session spécifique pour un étudiant (pour stats)
    // Note: nécessite une jointure complexe, souvent mieux géré via ResultatUE
}