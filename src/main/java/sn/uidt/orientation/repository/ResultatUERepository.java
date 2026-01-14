package sn.uidt.orientation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.uidt.orientation.constants.Semestre;
import sn.uidt.orientation.model.student.ResultatUE;

public interface ResultatUERepository extends JpaRepository<ResultatUE, Long> {

    /**
     * Récupère les résultats d'un étudiant pour un Semestre Pédagogique donné.
     * MISE À JOUR : Traverse ResultatUE -> InscriptionSemestrielle -> InscriptionAnnuelle -> Etudiant
     */
    @Query("SELECT r FROM ResultatUE r " +
           "JOIN r.inscriptionSemestrielle isem " +
           "JOIN isem.inscriptionAnnuelle ia " +
           "WHERE ia.etudiant.id = :etudiantId " +
           "AND isem.semestre = :semestre")
    List<ResultatUE> findByEtudiantAndSemestre(
        @Param("etudiantId") Long etudiantId, 
        @Param("semestre") Semestre semestre
    );

    /**
     * Capitalisation : Vérifier si l'étudiant a DÉJÀ validé une UE spécifique.
     * MISE À JOUR : Traverse ResultatUE -> InscriptionSemestrielle -> InscriptionAnnuelle -> Etudiant
     */
    @Query("SELECT r FROM ResultatUE r " +
           "JOIN r.inscriptionSemestrielle isem " +
           "JOIN isem.inscriptionAnnuelle ia " +
           "WHERE ia.etudiant.id = :etudiantId " +
           "AND r.ue.code = :codeUE " +
           "AND (r.statut = 'VALIDE' OR r.statut = 'ACQUIS_ANTERIEUR')")
    List<ResultatUE> findDejaValidee(
        @Param("etudiantId") Long etudiantId, 
        @Param("codeUE") String codeUE
    );
    
    /**
     * Récupère tous les résultats d'une inscription ANNUELLE.
     */
    @Query("SELECT r FROM ResultatUE r " +
           "JOIN r.inscriptionSemestrielle isem " +
           "WHERE isem.inscriptionAnnuelle.id = :inscriptionAnnuelleId")
    List<ResultatUE> findByInscriptionId(@Param("inscriptionAnnuelleId") Long inscriptionAnnuelleId);
}