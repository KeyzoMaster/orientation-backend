package sn.uidt.orientation.model.student;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import sn.uidt.orientation.model.maquette.Specialite;
import sn.uidt.orientation.model.security.Utilisateur;

@Entity
@Data
public class CandidatureMaster {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeFormation; // PUBLIQUE, PRIVEE
    private String verdict;       // ACCEPTE, REJETE

    @ManyToOne
    private Utilisateur etudiant;

    @ManyToOne
    private Specialite specialite;
}