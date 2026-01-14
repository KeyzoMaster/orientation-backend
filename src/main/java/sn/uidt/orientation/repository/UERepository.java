package sn.uidt.orientation.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.uidt.orientation.model.maquette.UE;

public interface UERepository extends JpaRepository<UE, Long> {
    
    // Essentiel pour l'initialisation et la vérification des dettes
    Optional<UE> findByCode(String code);

    // Récupérer toutes les UE d'un bloc de semestre spécifique
    List<UE> findByMaquetteSemestreId(Long maquetteSemestreId);
}