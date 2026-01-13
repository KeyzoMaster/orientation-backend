package sn.uidt.orientation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uidt.orientation.model.maquette.EC;

public interface ECRepository extends JpaRepository<EC, Long> {
}