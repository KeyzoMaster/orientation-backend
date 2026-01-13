package sn.uidt.orientation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.student.NoteEC;

public interface NoteECRepository extends JpaRepository<NoteEC, Long> {
    // Utile pour extraire toutes les notes d'un résultat d'UE spécifique
    List<NoteEC> findByResultatUEId(Long resultatUEId);
}