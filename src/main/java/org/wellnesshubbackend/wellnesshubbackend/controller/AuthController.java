package org.wellnesshubbackend.wellnesshubbackend.controller;


import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wellnesshubbackend.wellnesshubbackend.dto.*;
import org.wellnesshubbackend.wellnesshubbackend.model.*;
import org.wellnesshubbackend.wellnesshubbackend.repository.ResetTokenRepository;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;
import org.wellnesshubbackend.wellnesshubbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wellnesshubbackend.wellnesshubbackend.service.EmployeeService;
import org.wellnesshubbackend.wellnesshubbackend.service.ExpertService;
import org.wellnesshubbackend.wellnesshubbackend.service.HrPersonnelService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmployeeService employeeService;
    private final ExpertService expertService;
    private final HrPersonnelService hrPersonnelService;


    @PostMapping("/registerEmployee")
    public ResponseEntity<Employee> registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request) {
        Employee employee = employeeService.registerEmployee(request);
        return ResponseEntity.ok(employee);
    }
    @PostMapping("/registerExpert")
    public ResponseEntity<Expert> registerExpert(@Valid @RequestBody RegisterExpertRequest request) {
        Expert expert = expertService.registerExpert(request);
        return ResponseEntity.ok(expert);
    }
    @PostMapping("/registerHrPersonnel")
    public ResponseEntity<HrPersonnel> registerHrPersonnel(@Valid @RequestBody RegisterHrPersonnelRequest request) {
        HrPersonnel hrPersonnel = hrPersonnelService.registerHrPersonnel(request);
        return ResponseEntity.ok(hrPersonnel);
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


    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestReset(@RequestBody ResetRequest request) {
        authService.processRequest(request.getEmail());
        return ResponseEntity.ok("If the email is registered, you'll get a reset link");
    }


    @GetMapping("/reset-password")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        Optional<ResetToken> tokenOpt = resetTokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }

        return ResponseEntity.ok("Token is valid");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<ResetToken> tokenOpt = resetTokenRepository.findByToken(request.getToken());

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is invalid or expired");
        }

        ResetToken tokenRecord = tokenOpt.get();
        User user = tokenRecord.getUser();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetTokenRepository.delete(tokenRecord);

        return ResponseEntity.ok("Password updated");
    }
}