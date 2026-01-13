package sn.uidt.orientation.model.student;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import sn.uidt.orientation.model.maquette.EC;

@Entity
@Data
public class NoteEC {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double note;
    private String session; // NORMALE, RATTRAPAGE

    @ManyToOne
    private EC ec;

    @ManyToOne
    @JsonBackReference
    private ResultatUE resultatUE;
}