package sn.uidt.orientation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.maquette.UE;

public interface UERepository extends JpaRepository<UE, Long> {
    // Utile pour vérifier l'existence d'une UE par son code (ex: INF111)
}