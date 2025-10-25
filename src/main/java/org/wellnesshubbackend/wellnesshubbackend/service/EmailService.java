package org.wellnesshubbackend.wellnesshubbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.mail.from:malekeljendoubi@gmail.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String token) {
        try {
            String resetUrl = frontendUrl + "?token=" + token;
            String body = "You requested a password reset.Click the link below to reset your password.\n" + resetUrl;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset");
            message.setText(body);
            message.setFrom(fromEmail);

            log.info("Sending password reset email to: {}", email);
            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendWelcomeEmail(String email, String firstName, String lastName, UserType userType, String token) {
        try {
            log.info("Preparing to send welcome email to: {} {} ({})", firstName, lastName, email);

            String dashboardUrl = getDashboardUrl(userType, token);
            String subject = "Welcome to WellnessHub!";
            String body = buildWelcomeEmailBody(firstName, lastName, userType, dashboardUrl);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(fromEmail);

            log.info("Sending welcome email to: {} with token: {}", email, token.substring(0, 8) + "...");
            log.debug("Email from: {}, to: {}, subject: {}", fromEmail, email, subject);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", email, e);
            // Ne pas lancer d'exception pour ne pas bloquer l'enregistrement
            // mais logger l'erreur pour diagnostic
        }
    }

    private String getDashboardUrl(UserType userType, String token) {
        // Pour l'instant, pointer vers l'endpoint de vérification backend
        String baseUrl = "http://localhost:8084"; // URL du backend
        return baseUrl + "/api/email-verification/verify?token=" + token;
    }

    private String buildWelcomeEmailBody(String firstName, String lastName, UserType userType, String dashboardUrl) {
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(firstName).append(" ").append(lastName).append(",\n\n");

        switch (userType) {
            case EMPLOYEE:
                body.append("Your employee account has been created successfully. You can now access wellness resources and connect with experts.\n\n");
                body.append("As an employee, you can:\n");
                body.append("• Access wellness programs and resources\n");
                body.append("• Schedule sessions with wellness experts\n");
                body.append("• Track your wellness progress\n");
                body.append("• Connect with your wellness community\n\n");
                break;
            case EXPERT:
                body.append("Your expert account has been created successfully. You can now manage your schedule and connect with employees.\n\n");
                body.append("As a wellness expert, you can:\n");
                body.append("• Manage your availability and schedule\n");
                body.append("• Conduct wellness sessions with employees\n");
                body.append("• Create assessments and track progress\n");
                body.append("• Communicate with your clients\n\n");
                break;
            case HR_PERSONNEL:
                body.append("Your HR personnel account has been created successfully. You can now manage wellness programs and employee engagement.\n\n");
                body.append("As HR personnel, you can:\n");
                body.append("• Manage company wellness programs\n");
                body.append("• Monitor employee wellness engagement\n");
                body.append("• Generate wellness reports and analytics\n");
                body.append("• Coordinate with wellness experts\n\n");
                break;
        }

        body.append("Click the link below to verify your account and access your dashboard:\n");
        body.append(dashboardUrl).append("\n\n");
        body.append("This link will expire in 24 hours.\n\n");
        body.append("Best regards,\n");
        body.append("The WellnessHub Team");

        return body.toString();
    }
}
