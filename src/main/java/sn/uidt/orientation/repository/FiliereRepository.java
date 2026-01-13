package sn.uidt.orientation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.maquette.Filiere;

public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    Optional<Filiere> findByCode(String code);
}