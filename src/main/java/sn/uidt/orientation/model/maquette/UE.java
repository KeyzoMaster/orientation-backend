package sn.uidt.orientation.model.maquette;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
public class UE {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String libelle;
    private int credits;
    private double coefficient;
    private String domaine; // Pour Prolog : 'dev', 'reseau', etc.

    @OneToMany(mappedBy = "ue", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<EC> ecs;
}
