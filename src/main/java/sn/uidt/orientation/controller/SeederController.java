package sn.uidt.orientation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.service.DataSeederService;

@RestController
@RequestMapping("/api/v1/admin/seed")
@RequiredArgsConstructor
// Sécurité : Seul l'admin peut peupler la base
@PreAuthorize("hasRole('ADMIN')")
public class SeederController {

    private final DataSeederService seederService;

    @PostMapping("/anciens")
    public ResponseEntity<String> genererAnciens(@RequestParam(defaultValue = "50") int nombre) {
        try {
            seederService.seederAnciensEtudiants(nombre);
            return ResponseEntity.ok("Succès : " + nombre + " anciens étudiants générés avec parcours complets.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur lors du seeding : " + e.getMessage());
        }
    }
    
    @PostMapping("/parcours")
    public ResponseEntity<String> genererParcours(@RequestParam String email){
    	 try {
             seederService.seederParcoursEtudiant(email);
             return ResponseEntity.ok("Succès : parcours généré pour " + email);
         } catch (Exception e) {
             e.printStackTrace();
             return ResponseEntity.internalServerError().body("Erreur lors du seeding : " + e.getMessage());
         }
    }
    
}