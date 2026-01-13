package sn.uidt.orientation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.model.maquette.Filiere;
import sn.uidt.orientation.model.maquette.Specialite;
import sn.uidt.orientation.model.maquette.UE;
import sn.uidt.orientation.repository.FiliereRepository;
import sn.uidt.orientation.repository.SpecialiteRepository;
import sn.uidt.orientation.repository.UERepository;

@Service
@RequiredArgsConstructor
public class MaquetteService {

    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final UERepository ueRepository;

    public List<Filiere> getAllFilieres() {
        return filiereRepository.findAll();
    }

    @Transactional
    public Filiere createFiliere(Filiere filiere) {
        return filiereRepository.save(filiere);
    }

    public List<Specialite> getSpecialitesByFiliere(Long filiereId) {
        return specialiteRepository.findByFiliereId(filiereId);
    }
    
    @Transactional
    public Specialite createSpecialite(Specialite specialite) {
        // On s'assure que la filière associée existe bien
        return specialiteRepository.save(specialite);
    }

    @Transactional
    public UE createUE(UE ue) {
        // Logique pour s'assurer que les ECs sont liés à l'UE
        if (ue.getEcs() != null) {
            ue.getEcs().forEach(ec -> ec.setUe(ue));
        }
        return ueRepository.save(ue);
    }
}