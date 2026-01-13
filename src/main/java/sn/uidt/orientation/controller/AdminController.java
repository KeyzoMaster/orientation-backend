package sn.uidt.orientation.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.model.maquette.Filiere;
import sn.uidt.orientation.model.maquette.Specialite;
import sn.uidt.orientation.model.maquette.UE;
import sn.uidt.orientation.service.MaquetteService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MaquetteService maquetteService;

    // Récupérer toutes les filières
    @GetMapping("/filieres")
    public ResponseEntity<List<Filiere>> getFilieres() {
        return ResponseEntity.ok(maquetteService.getAllFilieres());
    }

    // AJOUT : Créer une filière (Nécessaire pour le test [3] de votre script)
    @PostMapping("/filieres")
    public ResponseEntity<Filiere> ajouterFiliere(@RequestBody Filiere filiere) {
        return ResponseEntity.ok(maquetteService.createFiliere(filiere));
    }

    // Créer une spécialité
    @PostMapping("/specialites")
    public ResponseEntity<Specialite> ajouterSpecialite(@RequestBody Specialite spec) {
        // Note: Assurez-vous que l'ID de la filière est passé dans le JSON
        return ResponseEntity.ok(maquetteService.createSpecialite(spec));
    }

    // Créer une UE (Fonctionne déjà d'après vos logs)
    @PostMapping("/ues")
    public ResponseEntity<UE> ajouterUE(@RequestBody UE ue) {
        return ResponseEntity.ok(maquetteService.createUE(ue));
    }
}