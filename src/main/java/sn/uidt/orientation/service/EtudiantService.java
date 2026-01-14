package sn.uidt.orientation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.model.student.InscriptionAnnuelle;
import sn.uidt.orientation.repository.InscriptionAnnuelleRepository;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final ExpertService expertService;

    /**
     * Récupère le parcours chronologique complet et structuré.
     * Utile pour l'affichage front-end "Timeline".
     */
    public List<InscriptionAnnuelle> getParcoursAcademique(Long etudiantId) {
        // Grâce au Repository mis à jour, cela retourne la liste triée par année (L1, L2...)
        // JPA gère le chargement des ResultatsUE et NotesEC liés via @JsonManagedReference
        return inscriptionRepository.findByEtudiantIdOrderByAnneeAcademiqueAsc(etudiantId);
    }

    /**
     * Lance la simulation d'orientation basée sur les notes actuelles
     */
    public String simulerOrientationMaster(Long etudiantId) {
        return expertService.analyserParcours(etudiantId);
    }
}