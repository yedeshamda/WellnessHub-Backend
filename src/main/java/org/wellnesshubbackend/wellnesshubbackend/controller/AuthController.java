package org.wellnesshubbackend.wellnesshubbackend.controller;


import org.wellnesshubbackend.wellnesshubbackend.dto.AuthRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.AuthResponse;
import org.wellnesshubbackend.wellnesshubbackend.dto.RegisterRequest;
import org.wellnesshubbackend.wellnesshubbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().body("Déconnexion réussie");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.substring(7);
        return ResponseEntity.ok(authService.refreshToken(token));
    }
}