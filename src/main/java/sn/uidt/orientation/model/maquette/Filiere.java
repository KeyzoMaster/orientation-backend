package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import nécessaire
import java.util.List;

@Entity
@Data
public class Filiere {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String code;
    private String libelle;

    // Ajoutez ceci pour permettre la relation JPA tout en évitant la boucle JSON
    @OneToMany(mappedBy = "filiere")
    @JsonIgnore 
    private List<Specialite> specialites;
}