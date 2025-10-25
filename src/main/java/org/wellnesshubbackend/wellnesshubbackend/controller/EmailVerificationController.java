package org.wellnesshubbackend.wellnesshubbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wellnesshubbackend.wellnesshubbackend.model.EmailVerificationToken;
import org.wellnesshubbackend.wellnesshubbackend.model.User;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;
import org.wellnesshubbackend.wellnesshubbackend.service.EmailVerificationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        Optional<EmailVerificationToken> verificationToken = emailVerificationService.findByToken(token);

        if (verificationToken.isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Token invalide"));
        }

        EmailVerificationToken emailToken = verificationToken.get();

        if (!emailToken.isValid()) {
            String message = emailToken.isExpired() ? "Token expiré" : "Token déjà utilisé";
            return ResponseEntity.badRequest().body(createErrorResponse(message));
        }

        // Marquer l'utilisateur comme vérifié
        User user = emailToken.getUser();
        user.setUserVerified(true);
        emailVerificationService.markUserAsVerified(user);

        // Marquer le token comme utilisé
        emailVerificationService.markTokenAsUsed(emailToken);

        // Retourner les informations pour redirection vers le dashboard approprié
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Email vérifié avec succès");
        response.put("user", createUserInfo(emailToken));
        response.put("redirectUrl", getDashboardUrl(emailToken.getUser().getUserType()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkTokenValidity(@RequestParam String token) {
        boolean isValid = emailVerificationService.isValidToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);

        if (isValid) {
            Optional<EmailVerificationToken> verificationToken = emailVerificationService.findByToken(token);
            if (verificationToken.isPresent()) {
                EmailVerificationToken emailToken = verificationToken.get();
                response.put("user", createUserInfo(emailToken));
                response.put("expiresAt", emailToken.getExpiresAt());
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/{userType}")
    public ResponseEntity<String> getDashboard(@PathVariable String userType) {
        // Pages de dashboard temporaires pour tester
        switch (userType.toUpperCase()) {
            case "EMPLOYEE":
                return ResponseEntity.ok(createEmployeeDashboardHtml());
            case "EXPERT":
                return ResponseEntity.ok(createExpertDashboardHtml());
            case "HR_PERSONNEL":
                return ResponseEntity.ok(createHrDashboardHtml());
            default:
                return ResponseEntity.badRequest().body("Type d'utilisateur invalide");
        }
    }

    @GetMapping("/get-token/{email}")
    public ResponseEntity<?> getTokenByEmail(@PathVariable String email) {
        try {
            // Chercher l'utilisateur par email dans la base
            Optional<EmailVerificationToken> token = emailVerificationService.findByUserEmail(email);

            if (token.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("token", token.get().getToken());
                response.put("expiresAt", token.get().getExpiresAt());
                response.put("used", token.get().isUsed());
                response.put("valid", token.get().isValid());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("Aucun token trouvé pour cet email"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Erreur lors de la recherche du token"));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }

    private Map<String, Object> createUserInfo(EmailVerificationToken token) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", token.getUser().getId());
        userInfo.put("username", token.getUser().getUsername());
        userInfo.put("firstName", token.getUser().getFirstName());
        userInfo.put("lastName", token.getUser().getLastName());
        userInfo.put("email", token.getUser().getEmail());
        userInfo.put("userType", token.getUser().getUserType());
        return userInfo;
    }

    private String getDashboardUrl(UserType userType) {
        switch (userType) {
            case EMPLOYEE:
                return "/api/email-verification/dashboard/employee";
            case EXPERT:
                return "/api/email-verification/dashboard/expert";
            case HR_PERSONNEL:
                return "/api/email-verification/dashboard/hr_personnel";
            default:
                return "/api/email-verification/dashboard/default";
        }
    }

    private String createEmployeeDashboardHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Employee Dashboard - WellnessHub</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 15px; margin-bottom: 30px; }
                    .welcome { background: linear-gradient(135deg, #3498db, #2980b9); color: white; padding: 20px; border-radius: 8px; margin-bottom: 30px; }
                    .feature-list { list-style: none; padding: 0; }
                    .feature-list li { background: #ecf0f1; margin: 10px 0; padding: 15px; border-radius: 5px; border-left: 4px solid #3498db; }
                    .btn { background: #3498db; color: white; padding: 12px 24px; border: none; border-radius: 5px; text-decoration: none; display: inline-block; margin: 10px 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1 class="header">🏢 Employee Dashboard - WellnessHub</h1>
                    <div class="welcome">
                        <h2>Bienvenue dans votre espace employé !</h2>
                        <p>Votre compte a été vérifié avec succès. Vous avez maintenant accès à toutes les ressources de bien-être.</p>
                    </div>
                    
                    <h3>📋 Vos fonctionnalités disponibles :</h3>
                    <ul class="feature-list">
                        <li>🎯 <strong>Programmes de bien-être</strong> - Accédez aux ressources et programmes personnalisés</li>
                        <li>👥 <strong>Sessions avec des experts</strong> - Planifiez des rendez-vous avec nos spécialistes</li>
                        <li>📊 <strong>Suivi de progression</strong> - Suivez vos objectifs et progrès wellness</li>
                        <li>💬 <strong>Communauté wellness</strong> - Connectez-vous avec d'autres employés</li>
                    </ul>
                    
                    <div style="margin-top: 30px; text-align: center;">
                        <a href="#" class="btn">🚀 Commencer mon parcours wellness</a>
                        <a href="#" class="btn">📅 Voir les experts disponibles</a>
                    </div>
                    
                    <p style="text-align: center; margin-top: 30px; color: #7f8c8d;">
                        <em>Cette page est temporaire pour les tests. L'interface complète sera bientôt disponible.</em>
                    </p>
                </div>
            </body>
            </html>
            """;
    }

    private String createExpertDashboardHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Expert Dashboard - WellnessHub</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { color: #2c3e50; border-bottom: 2px solid #27ae60; padding-bottom: 15px; margin-bottom: 30px; }
                    .welcome { background: linear-gradient(135deg, #27ae60, #229954); color: white; padding: 20px; border-radius: 8px; margin-bottom: 30px; }
                    .feature-list { list-style: none; padding: 0; }
                    .feature-list li { background: #ecf0f1; margin: 10px 0; padding: 15px; border-radius: 5px; border-left: 4px solid #27ae60; }
                    .btn { background: #27ae60; color: white; padding: 12px 24px; border: none; border-radius: 5px; text-decoration: none; display: inline-block; margin: 10px 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1 class="header">👨‍⚕️ Expert Dashboard - WellnessHub</h1>
                    <div class="welcome">
                        <h2>Bienvenue Expert !</h2>
                        <p>Votre compte expert a été vérifié avec succès. Vous pouvez maintenant commencer à accompagner vos clients.</p>
                    </div>
                    
                    <h3>🔧 Vos outils d'expert :</h3>
                    <ul class="feature-list">
                        <li>📅 <strong>Gestion des disponibilités</strong> - Configurez votre planning et vos créneaux</li>
                        <li>🎯 <strong>Sessions wellness</strong> - Animez des séances avec vos clients employés</li>
                        <li>📋 <strong>Évaluations et suivi</strong> - Créez des bilans et suivez les progrès</li>
                        <li>💬 <strong>Communication clients</strong> - Échangez avec vos participants</li>
                    </ul>
                    
                    <div style="margin-top: 30px; text-align: center;">
                        <a href="#" class="btn">⚙️ Configurer mon profil expert</a>
                        <a href="#" class="btn">📊 Voir mes statistiques</a>
                    </div>
                    
                    <p style="text-align: center; margin-top: 30px; color: #7f8c8d;">
                        <em>Cette page est temporaire pour les tests. L'interface complète sera bientôt disponible.</em>
                    </p>
                </div>
            </body>
            </html>
            """;
    }

    private String createHrDashboardHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>HR Dashboard - WellnessHub</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { color: #2c3e50; border-bottom: 2px solid #e74c3c; padding-bottom: 15px; margin-bottom: 30px; }
                    .welcome { background: linear-gradient(135deg, #e74c3c, #c0392b); color: white; padding: 20px; border-radius: 8px; margin-bottom: 30px; }
                    .feature-list { list-style: none; padding: 0; }
                    .feature-list li { background: #ecf0f1; margin: 10px 0; padding: 15px; border-radius: 5px; border-left: 4px solid #e74c3c; }
                    .btn { background: #e74c3c; color: white; padding: 12px 24px; border: none; border-radius: 5px; text-decoration: none; display: inline-block; margin: 10px 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1 class="header">👔 HR Dashboard - WellnessHub</h1>
                    <div class="welcome">
                        <h2>Bienvenue dans l'espace RH !</h2>
                        <p>Votre compte RH a été vérifié avec succès. Pilotez maintenant le bien-être de vos équipes.</p>
                    </div>
                    
                    <h3>📊 Vos outils de gestion RH :</h3>
                    <ul class="feature-list">
                        <li>🏢 <strong>Programmes d'entreprise</strong> - Gérez les initiatives wellness de votre société</li>
                        <li>👥 <strong>Engagement employés</strong> - Suivez la participation et la satisfaction</li>
                        <li>📈 <strong>Rapports et analytics</strong> - Analysez les données de bien-être de vos équipes</li>
                        <li>🤝 <strong>Coordination experts</strong> - Collaborez avec les spécialistes wellness</li>
                    </ul>
                    
                    <div style="margin-top: 30px; text-align: center;">
                        <a href="#" class="btn">🎯 Lancer un programme wellness</a>
                        <a href="#" class="btn">📊 Voir les rapports d'engagement</a>
                    </div>
                    
                    <p style="text-align: center; margin-top: 30px; color: #7f8c8d;">
                        <em>Cette page est temporaire pour les tests. L'interface complète sera bientôt disponible.</em>
                    </p>
                </div>
            </body>
            </html>
            """;
    }
}
