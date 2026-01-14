package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "specialites")
@EqualsAndHashCode(exclude = "specialites")
@Table(name = "filiere")
public class Filiere {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String code;    // INFO
    private String libelle; // Informatique

    @OneToMany(mappedBy = "filiere")
    @JsonIgnore
    private List<Specialite> specialites;
}