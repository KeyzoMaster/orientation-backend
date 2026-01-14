package sn.uidt.orientation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.constants.Role;
import sn.uidt.orientation.dto.AuthResponse;
import sn.uidt.orientation.dto.LoginRequest;
import sn.uidt.orientation.dto.RegisterRequest;
import sn.uidt.orientation.model.security.Utilisateur;
import sn.uidt.orientation.repository.UtilisateurRepository;
import sn.uidt.orientation.security.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UtilisateurRepository utilisateurRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
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
        
        Utilisateur user = utilisateurRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), request.email()));
    }
}