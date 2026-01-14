package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.*;
import sn.uidt.orientation.constants.Semestre;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"ues", "specialites"})
@EqualsAndHashCode(exclude = {"ues", "specialites"})
@Table(name = "maquette_semestre")
public class MaquetteSemestre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle; // "Semestre 5 GL"

    @Enumerated(EnumType.STRING)
    private Semestre semestre; // L_S5

    @OneToMany(mappedBy = "maquetteSemestre", cascade = CascadeType.ALL)
    private List<UE> ues;

    @ManyToMany(mappedBy = "maquettes")
    @JsonIgnore
    private List<Specialite> specialites;
}