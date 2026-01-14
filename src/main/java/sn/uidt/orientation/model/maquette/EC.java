package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "ue")
@EqualsAndHashCode(exclude = "ue")
@Table(name = "ec")
public class EC {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "ue_id") // Force la colonne SQL correcte
    @JsonBackReference
    private UE ue;
}