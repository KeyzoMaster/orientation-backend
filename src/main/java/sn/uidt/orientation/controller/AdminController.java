package sn.uidt.orientation.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.constants.TypeSession;
import sn.uidt.orientation.dto.*;
import sn.uidt.orientation.model.maquette.*;
import sn.uidt.orientation.model.student.*;
import sn.uidt.orientation.repository.SpecialiteRepository; // Ajout nécessaire
import sn.uidt.orientation.service.AdminPedagogiqueService;
import sn.uidt.orientation.service.MaquetteService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MaquetteService maquetteService;
    private final AdminPedagogiqueService adminPedagogiqueService;
    private final SpecialiteRepository specialiteRepository; // Injection nécessaire

    // --- GESTION MAQUETTE ---

    @GetMapping("/filieres")
    public ResponseEntity<List<Filiere>> getFilieres() {
        return ResponseEntity.ok(maquetteService.getAllFilieres());
    }

    @PostMapping("/filieres")
    public ResponseEntity<Filiere> ajouterFiliere(@RequestBody Filiere filiere) {
        return ResponseEntity.ok(maquetteService.createFiliere(filiere));
    }

    @PostMapping("/filieres/{filiereId}/specialites")
    public ResponseEntity<Specialite> ajouterSpecialite(@PathVariable Long filiereId, @RequestBody Specialite spec) {
        return ResponseEntity.ok(maquetteService.createSpecialite(filiereId, spec));
    }
    
    @PostMapping("/specialites/{specId}/semestres/{maquetteId}")
    public ResponseEntity<Void> lierSemestreSpecialite(@PathVariable Long specId, @PathVariable Long maquetteId) {
        maquetteService.addSemestreToSpecialite(specId, maquetteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/maquettes/{maquetteId}/ues")
    public ResponseEntity<UE> ajouterUE(@PathVariable Long maquetteId, @RequestBody UE ue) {
        return ResponseEntity.ok(maquetteService.addUEToSemestre(maquetteId, ue));
    }

    // --- GESTION PEDAGOGIQUE ---

    @PostMapping("/inscriptions")
    public ResponseEntity<String> inscrireEtudiant(@RequestBody InscriptionAdministrativeDto dto) {
        // 1. Récupération de la spécialité
        Specialite specialite = specialiteRepository.findById(dto.specialiteId())
                .orElseThrow(() -> new RuntimeException("Spécialité introuvable"));

        // 2. Appel du service mis à jour (plus besoin de Semestre enum)
        adminPedagogiqueService.inscrireEtudiant(
            dto.etudiantId(), 
            dto.annee(), 
            specialite
        );
        
        return ResponseEntity.ok("Inscription annuelle réalisée avec succès (Semestres générés).");
    }

    @PostMapping("/notes")
    public ResponseEntity<NoteDto> saisirNote(@RequestBody SaisieNoteDto dto) {
        NoteEC note = adminPedagogiqueService.saisirNote(
            dto.inscriptionId(),
            dto.codeUE(),
            dto.nomEC(),
            dto.note(),
            TypeSession.valueOf(dto.session())
        );
        return ResponseEntity.ok(NoteDto.fromEntity(note));
    }
    
    @PostMapping("/anciens/import")
    public ResponseEntity<String> importerAncien(@RequestBody ImportAncienDto dto) {
        // TODO: Implémentation du mapping DTO -> Entités pour l'import de masse
        return ResponseEntity.ok("Fonctionnalité d'import en cours de développement");
    }
}