package sn.uidt.orientation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.model.maquette.*;
import sn.uidt.orientation.repository.*;

@Service
@RequiredArgsConstructor
public class MaquetteService {

    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final MaquetteSemestreRepository maquetteSemestreRepository;
    private final UERepository ueRepository;
    private final ECRepository ecRepository;

    // --- GESTION FILIERES ---
    public List<Filiere> getAllFilieres() {
        return filiereRepository.findAll();
    }

    @Transactional
    public Filiere createFiliere(Filiere filiere) {
        return filiereRepository.save(filiere);
    }

    // --- GESTION SPECIALITES ---
    @Transactional
    public Specialite createSpecialite(Long filiereId, Specialite specialite) {
        Filiere filiere = filiereRepository.findById(filiereId)
            .orElseThrow(() -> new RuntimeException("Filière non trouvée"));
        specialite.setFiliere(filiere);
        return specialiteRepository.save(specialite);
    }

    /**
     * Associe un semestre (ex: S5 GL) à une spécialité (ex: GL)
     */
    @Transactional
    public void addSemestreToSpecialite(Long specialiteId, Long maquetteId) {
        Specialite spec = specialiteRepository.findById(specialiteId).orElseThrow();
        MaquetteSemestre maquette = maquetteSemestreRepository.findById(maquetteId).orElseThrow();
        
        spec.getMaquettes().add(maquette);
        specialiteRepository.save(spec);
    }

    // --- GESTION UEs et ECs ---
    @Transactional
    public UE addUEToSemestre(Long maquetteId, UE ue) {
        MaquetteSemestre ms = maquetteSemestreRepository.findById(maquetteId)
            .orElseThrow(() -> new RuntimeException("Maquette Semestre introuvable"));
        
        ue.setMaquetteSemestre(ms);
        
        // Sauvegarde de l'UE et de ses ECs en cascade
        if (ue.getEcs() != null) {
            ue.getEcs().forEach(ec -> ec.setUe(ue));
        }
        return ueRepository.save(ue);
    }

    @Transactional
    public EC addECToUE(Long ueId, EC ec) {
        UE ue = ueRepository.findById(ueId)
            .orElseThrow(() -> new RuntimeException("UE introuvable"));
        ec.setUe(ue);
        return ecRepository.save(ec);
    }
}