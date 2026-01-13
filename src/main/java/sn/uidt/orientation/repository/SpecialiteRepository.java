package sn.uidt.orientation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.maquette.Specialite;

public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    List<Specialite> findByFiliereId(Long filiereId);
    Optional<Specialite> findByCode(String code);
}