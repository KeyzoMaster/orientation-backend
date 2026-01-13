package sn.uidt.orientation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.student.CandidatureMaster;

public interface CandidatureMasterRepository extends JpaRepository<CandidatureMaster, Long> {
    // Pour l'analyse historique : trouver tous les verdicts pour une spécialité donnée
    List<CandidatureMaster> findBySpecialiteIdAndTypeFormation(Long specId, String type);
    
    // Pour l'étudiant : voir ses propres candidatures
    List<CandidatureMaster> findByEtudiantId(Long etudiantId);
}