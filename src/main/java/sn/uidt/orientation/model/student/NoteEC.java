package sn.uidt.orientation.model.student;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import sn.uidt.orientation.constants.TypeSession;
import sn.uidt.orientation.model.maquette.EC;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "resultatUE")
@EqualsAndHashCode(exclude = "resultatUE")
@Table(name = "note_ec")
public class NoteEC {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double note;
    
    @Enumerated(EnumType.STRING)
    private TypeSession session; 

    private boolean isAbsenceJustifiee;

    @ManyToOne
    @JoinColumn(name = "ec_id") // Match SQL: ec_id
    private EC ec;

    @ManyToOne
    @JoinColumn(name = "resultat_ue_id") // Match SQL: resultat_ue_id
    @JsonBackReference
    private ResultatUE resultatUE;
}