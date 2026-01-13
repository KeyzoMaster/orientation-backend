package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Specialite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code; 
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "filiere_id") // Bonne pratique pour nommer la colonne
    private Filiere filiere;
}