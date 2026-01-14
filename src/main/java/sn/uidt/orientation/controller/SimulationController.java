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
    private final ObjectMapper objectMapper;

    @PostMapping("/lancer")
    public ResponseEntity<?> demanderSimulation(
            @AuthenticationPrincipal UserDetails currentUser) {
        
        try {
            Long etudiantId = expertService.getEtudiantIdByEmail(currentUser.getUsername());
            String jsonResult = expertService.analyserParcours(etudiantId);
            
            // Si le résultat est un message d'erreur JSON simple
            if (jsonResult.contains("\"error\"") || jsonResult.contains("\"message\"")) {
                 return ResponseEntity.badRequest().body(jsonResult);
            }

            RecommandationDto recommendation = objectMapper.readValue(jsonResult, RecommandationDto.class);
            return ResponseEntity.ok(recommendation);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Erreur lors de la simulation : " + e.getMessage());
        }
    }
}