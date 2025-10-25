package org.wellnesshubbackend.wellnesshubbackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterExpertRequest {
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String firstName;

    @NotBlank(message = "Le nom de famille est requis")
    @Size(max = 100, message = "Le nom de famille ne peut pas dépasser 100 caractères")
    private String lastName;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    @Min(value = 18, message = "L'âge minimum est 18 ans")
    @Max(value = 120, message = "L'âge maximum est 120 ans")
    private Integer age;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateOfBirth;

    @DecimalMin(value = "-90.0", message = "La latitude doit être entre -90 et 90")
    @DecimalMax(value = "90.0", message = "La latitude doit être entre -90 et 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "La longitude doit être entre -180 et 180")
    @DecimalMax(value = "180.0", message = "La longitude doit être entre -180 et 180")
    private Double longitude;

    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Le numéro de téléphone doit être valide (8-15 chiffres)")
    private String phoneNumber;

    @Size(max = 500, message = "L'URL de l'avatar ne peut pas dépasser 500 caractères")
    private String avatarUrl;

    @NotBlank(message = "La spécialisation est requise")
    private String specialization;
}
