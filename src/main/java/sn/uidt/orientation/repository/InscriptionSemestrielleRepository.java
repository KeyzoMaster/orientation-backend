package sn.uidt.orientation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.uidt.orientation.model.student.InscriptionSemestrielle;

public interface InscriptionSemestrielleRepository extends JpaRepository<InscriptionSemestrielle, Long> {
    
    // Retrouver les semestres d'une inscription annuelle spécifique
    List<InscriptionSemestrielle> findByInscriptionAnnuelleId(Long inscriptionAnnuelleId);
}