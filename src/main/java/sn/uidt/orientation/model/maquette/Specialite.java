package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"filiere", "maquettes"})
@EqualsAndHashCode(exclude = {"filiere", "maquettes"})
@Table(name = "specialite")
public class Specialite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String code;    // L3_GL
    private String libelle; // Licence 3 Génie Logiciel

    @ManyToOne
    @JoinColumn(name = "filiere_id") // Important pour le SQL
    private Filiere filiere;

    @ManyToMany
    @JoinTable(
        name = "parcours_specialite",
        joinColumns = @JoinColumn(name = "specialite_id"),
        inverseJoinColumns = @JoinColumn(name = "maquette_id")
    )
    private List<MaquetteSemestre> maquettes;
}