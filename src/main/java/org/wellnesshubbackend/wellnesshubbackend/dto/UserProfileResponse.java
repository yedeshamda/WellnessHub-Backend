package org.wellnesshubbackend.wellnesshubbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UserType userType;
    private Integer age;
    private LocalDate dateOfBirth;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Champs spécifiques aux différents types d'utilisateurs
    private String specialization; // Pour Expert
    private String employeeNumber; // Pour Employee
    private String companyName;    // Pour HrPersonnel

    // Statut de vérification du compte
    private boolean userVerified;
}
