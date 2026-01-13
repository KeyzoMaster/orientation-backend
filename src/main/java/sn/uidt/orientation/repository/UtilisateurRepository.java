package sn.uidt.orientation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.security.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
}