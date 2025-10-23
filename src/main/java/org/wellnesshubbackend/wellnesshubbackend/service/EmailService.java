package org.wellnesshubbackend.wellnesshubbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private  String frontendUrl;

    @Value("${app.mail.from:no-reply@wellnesshub.com.tn}")
    private String fromEmail;


    public EmailService(JavaMailSender mailSender) {

        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String token) {


        String resetUrl = frontendUrl + "?token=" + token;

//        String resetUrl = "https://wellnesshub.com.tn/reset-password?token=" + token;
        String body = "You requested a password reset.Click the link below to reset your password.\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText(body);

        mailSender.send(message);
    }
}
