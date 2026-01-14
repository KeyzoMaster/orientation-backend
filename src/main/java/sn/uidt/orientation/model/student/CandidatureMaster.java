package sn.uidt.orientation.model.student;

import jakarta.persistence.*;
import lombok.*;
import sn.uidt.orientation.model.maquette.Specialite;
import sn.uidt.orientation.model.security.Utilisateur;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "candidature_master")
public class CandidatureMaster {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String typeFormation; // PUBLIQUE, PRIVEE
    private String verdict;       // ACCEPTE, REJETE

    @ManyToOne
    @JoinColumn(name = "etudiant_id") // Match SQL: etudiant_id
    private Utilisateur etudiant;

    @ManyToOne
    @JoinColumn(name = "specialite_id") // Match SQL: specialite_id
    private Specialite specialite;
}