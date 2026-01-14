package sn.uidt.orientation.model.student;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import sn.uidt.orientation.constants.Semestre;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inscription_semestrielle")
public class InscriptionSemestrielle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Semestre semestre; // Ex: L_S1

    private Double moyenneSemestre;
    private int creditsObtenus;
    private boolean estValide; // Décision du semestre (Validé ou Non)

    @ManyToOne
    @JoinColumn(name = "inscription_annuelle_id")
    @JsonBackReference
    private InscriptionAnnuelle inscriptionAnnuelle;

    @OneToMany(mappedBy = "inscriptionSemestrielle", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ResultatUE> resultatsUE;
}