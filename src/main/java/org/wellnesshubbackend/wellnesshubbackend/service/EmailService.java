package org.wellnesshubbackend.wellnesshubbackend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSenderImpl mailSender;

    public EmailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String token) {
        String resetUrl = "https://yourapp.com/reset-password?token=" + token;
        String body = "Click the link below to reset your password.\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText(body);

        mailSender.send(message);
    }
}
