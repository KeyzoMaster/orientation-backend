package sn.uidt.orientation.model.maquette;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EC {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
    private double coefficient;

    @ManyToOne
    @JsonBackReference
    private UE ue;
}