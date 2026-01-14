package sn.uidt.orientation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.constants.Semestre;
import sn.uidt.orientation.model.maquette.MaquetteSemestre;

public interface MaquetteSemestreRepository extends JpaRepository<MaquetteSemestre, Long> {
    
    // Trouver la maquette correspondant à un enum (ex: L_S1)
    // Note: Il peut y en avoir plusieurs (ex: S1 Tronc Commun, S5 GL, S5 SR)
    List<MaquetteSemestre> findBySemestre(Semestre semestre);
}