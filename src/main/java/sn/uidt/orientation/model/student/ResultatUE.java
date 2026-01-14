package sn.uidt.orientation.model.student;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import sn.uidt.orientation.constants.StatutResultat;
import sn.uidt.orientation.constants.TypeInscriptionUE;
import sn.uidt.orientation.model.maquette.UE;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resultat_ue")
public class ResultatUE {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Double moyenne; 

    @Enumerated(EnumType.STRING)
    private StatutResultat statut; 

    @Enumerated(EnumType.STRING)
    private TypeInscriptionUE typeInscription; 

    private boolean isReportDeNote; 

    @ManyToOne
    @JoinColumn(name = "ue_id")
    private UE ue;

    // CHANGEMENT ICI : Lien vers le semestre
    @ManyToOne
    @JoinColumn(name = "inscription_semestrielle_id")
    @JsonBackReference
    private InscriptionSemestrielle inscriptionSemestrielle;

    @OneToMany(mappedBy = "resultatUE", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<NoteEC> notesEC;
}