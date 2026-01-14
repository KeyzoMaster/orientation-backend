package sn.uidt.orientation.model.student;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import sn.uidt.orientation.model.maquette.Specialite;
import sn.uidt.orientation.model.security.Utilisateur;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inscription_annuelle")
public class InscriptionAnnuelle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int anneeAcademique; // 2024
    
    // On retire 'semestreActuel' car une année couvre 2 semestres
    
    private String cycle;           // LICENCE
    private String decisionConseil; // ADMIS, REDOUBLANT, AJAC
    private Double moyenneAnnuelle;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Utilisateur etudiant;

    @ManyToOne
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    // NOUVEAU LIEN
    @OneToMany(mappedBy = "inscriptionAnnuelle", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<InscriptionSemestrielle> inscriptionsSemestrielles;
}