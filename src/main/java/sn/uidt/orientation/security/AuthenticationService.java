package sn.uidt.orientation.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sn.uidt.orientation.constants.Role;
import sn.uidt.orientation.model.security.Utilisateur;
import sn.uidt.orientation.repository.UtilisateurRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(String email, String password, Role role) {
        var user = Utilisateur.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        repository.save(user);
        return jwtService.generateToken(user);
    }

    public String login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        var user = repository.findByEmail(email)
                .orElseThrow();
        return jwtService.generateToken(user);
    }
}