package sn.uidt.orientation.model.student;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import sn.uidt.orientation.model.maquette.Specialite;
import sn.uidt.orientation.model.security.Utilisateur;

@Entity
@Data
public class InscriptionAnnuelle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int annee;
    private String niveau; // L1, L2, L3...
    private String cycle;  // LICENCE, MASTER
    private String decisionConseil; // ADMIS, REDOUBLANT...

    @ManyToOne
    private Utilisateur etudiant;

    @ManyToOne
    private Specialite specialite;

    @OneToMany(mappedBy = "inscription", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ResultatUE> resultatsUE;
}