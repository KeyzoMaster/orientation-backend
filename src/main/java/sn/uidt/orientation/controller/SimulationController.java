package sn.uidt.orientation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.dto.RecommandationDto;
import sn.uidt.orientation.service.ExpertService;

@RestController
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final ExpertService expertService;
    private final ObjectMapper objectMapper; // Pour parser le JSON venant de Prolog

    @PostMapping("/lancer")
    public ResponseEntity<RecommandationDto> demanderSimulation(
            @AuthenticationPrincipal UserDetails currentUser) {
        
        // 1. Récupérer l'ID de l'étudiant via son email (username du JWT)
        Long etudiantId = expertService.getEtudiantIdByEmail(currentUser.getUsername());

        // 2. Appeler le service Prolog qui renvoie une String JSON
        String jsonResult = expertService.analyserParcours(etudiantId);

        try {
            // 3. Mapper le résultat brut de Prolog vers le DTO Java
            RecommandationDto recommendation = objectMapper.readValue(jsonResult, RecommandationDto.class);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}