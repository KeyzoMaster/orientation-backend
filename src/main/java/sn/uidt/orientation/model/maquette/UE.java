package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"maquetteSemestre", "ecs"})
@EqualsAndHashCode(exclude = {"maquetteSemestre", "ecs"})
@Table(name = "ue")
public class UE {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String code;       // INF351
    private String libelle;    // Programmation
    private int credits;       // 8
    private double coefficient; // 3.0
    private String domaine;    // dev

    @ManyToOne
    @JoinColumn(name = "maquette_id") // Force la colonne SQL correcte
    @JsonBackReference
    private MaquetteSemestre maquetteSemestre;

    @OneToMany(mappedBy = "ue", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<EC> ecs;
}