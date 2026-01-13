package sn.uidt.orientation.model.student;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import sn.uidt.orientation.model.maquette.UE;

@Entity
@Data
public class ResultatUE {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean validee;
    private boolean premiereFois;

    @ManyToOne
    private UE ue;

    @ManyToOne
    @JsonBackReference
    private InscriptionAnnuelle inscription;

    @OneToMany(mappedBy = "resultatUE", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<NoteEC> notesEC;
}