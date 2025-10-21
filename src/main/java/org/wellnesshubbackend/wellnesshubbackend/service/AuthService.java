package org.wellnesshubbackend.wellnesshubbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.wellnesshubbackend.wellnesshubbackend.dto.*;
import org.wellnesshubbackend.wellnesshubbackend.exception.*;
import org.wellnesshubbackend.wellnesshubbackend.model.*;
import org.wellnesshubbackend.wellnesshubbackend.repository.*;
import org.wellnesshubbackend.wellnesshubbackend.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.wellnesshubbackend.wellnesshubbackend.model.UserType.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ExpertRepository expertRepository;
    private final EmployeeRepository employeeRepository;
    private final HrPersonnelRepository hrPersonnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;


    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();


    private final ResetTokenRepository resetTokenRepository;
    @Autowired
    private EmailService emailService;


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Le nom d'utilisateur '" + request.getUsername() + "' existe déjà");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("L'email '" + request.getEmail() + "' existe déjà");
        }

        User user;

        // Créer l'utilisateur en fonction du type
        switch (request.getUserType()) {
            case EXPERT:
                Expert expert = new Expert();
                setCommonFields(expert, request);
                user = expertRepository.save(expert);
                break;

            case EMPLOYEE:
                Employee employee = new Employee();
                setCommonFields(employee, request);
                user = employeeRepository.save(employee);
                break;

            case HR_PERSONNEL:
                HrPersonnel hrPersonnel = new HrPersonnel();
                setCommonFields(hrPersonnel, request);
                user = hrPersonnelRepository.save(hrPersonnel);
                break;

            default:
                throw new InvalidUserTypeException("Type d'utilisateur invalide: " + request.getUserType());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username(user.getUsername())
                .email(user.getEmail())
                .userType(request.getUserType().toString())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur '" + request.getUsername() + "' non trouvé"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .username(user.getUsername())
                .email(user.getEmail())
                .userType(getUserType(user))
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtils.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtils.isTokenValid(refreshToken, userDetails)) {
            String newAccessToken = jwtUtils.generateToken(userDetails);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur '" + username + "' non trouvé"));

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(3600L)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .userType(getUserType(user))
                    .build();
        } else {
            throw new InvalidTokenException("Token de rafraîchissement invalide ou expiré");
        }
    }

    private void setCommonFields(User user, RegisterRequest request) {
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(request.getUserType());
    }

    public void logout(String token) {
        // Extraire le token du format "Bearer token"
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        // Vérifier que le token est valide
        String username = jwtUtils.extractUsername(jwtToken);

        if (username == null) {
            throw new InvalidTokenException("Token invalide");
        }

        // Note: Pour une implémentation plus robuste, vous pouvez :
        // - Ajouter le token à une blacklist dans Redis ou une base de données
        // - Supprimer les sessions actives de l'utilisateur
        // - Logger l'événement de déconnexion

        // Pour l'instant, la simple invalidation côté client suffit
        // car les tokens JWT sont stateless
    }

    private String getUserType(User user) {
        String className = user.getClass().getSimpleName();

        switch (className) {
            case "Expert":
                return "EXPERT";
            case "Employee":
                return "EMPLOYEE";
            case "HrPersonnel":
                return "HR_PERSONNEL";
            default:
                return "USER";
        }
    }


    public String makeResetToken() {
        byte[] randomBytes = new byte[32]; // 256-bit token
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public void processRequest(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String token = makeResetToken();

        ResetToken record = new ResetToken();
        record.setToken(token);
        record.setUser(user);
        record.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        resetTokenRepository.save(record);

        emailService.sendEmail(user.getEmail(), token);
    }



}