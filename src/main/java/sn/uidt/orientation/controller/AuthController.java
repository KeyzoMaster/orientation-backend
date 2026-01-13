package sn.uidt.orientation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.dto.AuthResponse;
import sn.uidt.orientation.dto.LoginRequest;
import sn.uidt.orientation.model.security.Role;
import sn.uidt.orientation.security.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Par défaut, on peut mettre ETUDIANT ou laisser le choix dans le DTO
        String token = authenticationService.register(
                request.email(), 
                request.password(), 
                Role.valueOf(request.role())
        );
        return ResponseEntity.ok(new AuthResponse(token, request.role(), request.email()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authenticationService.login(request.email(), request.password());
        
        // Pour simplifier le frontend, on renvoie aussi le rôle (à extraire du service si besoin)
        // Ici, j'utilise un retour simplifié, vous pouvez ajuster selon votre AuthenticationService
        return ResponseEntity.ok(new AuthResponse(token, "UNKNOWN", request.email()));
    }
}

// DTO spécifique pour l'inscription (peut être mis dans le package dto)
record RegisterRequest(String email, String password, String role) {}