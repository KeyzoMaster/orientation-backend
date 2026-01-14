package sn.uidt.orientation.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.dto.ParcoursDto;
import sn.uidt.orientation.model.student.InscriptionAnnuelle;
import sn.uidt.orientation.service.EtudiantService;
import sn.uidt.orientation.service.ExpertService;

@RestController
@RequestMapping("/api/v1/etudiant")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final ExpertService expertService;

    @GetMapping("/parcours")
    public ResponseEntity<ParcoursDto> getMonParcours(@AuthenticationPrincipal UserDetails currentUser) {
        Long etudiantId = expertService.getEtudiantIdByEmail(currentUser.getUsername());
        
        List<InscriptionAnnuelle> historique = etudiantService.getParcoursAcademique(etudiantId);
        
        // Utilisation de la méthode statique du DTO pour convertir proprement
        // Note: currentUser.getUsername() est l'email, on pourrait chercher le vrai nom en DB si besoin
        ParcoursDto dto = ParcoursDto.fromEntity(etudiantId, currentUser.getUsername(), historique);
        
        return ResponseEntity.ok(dto);
    }
}