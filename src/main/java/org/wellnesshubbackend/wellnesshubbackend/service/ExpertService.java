package org.wellnesshubbackend.wellnesshubbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wellnesshubbackend.wellnesshubbackend.dto.RegisterExpertRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UpdateUserRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UserProfileResponse;
import org.wellnesshubbackend.wellnesshubbackend.exception.UserAlreadyExistsException;
import org.wellnesshubbackend.wellnesshubbackend.exception.UserNotFoundException;
import org.wellnesshubbackend.wellnesshubbackend.model.EmailVerificationToken;
import org.wellnesshubbackend.wellnesshubbackend.model.Expert;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;
import org.wellnesshubbackend.wellnesshubbackend.repository.ExpertRepository;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpertService {

    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public Expert registerExpert(RegisterExpertRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Le nom d'utilisateur '" + request.getUsername() + "' existe déjà");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("L'email '" + request.getEmail() + "' existe déjà");
        }

        Expert expert = new Expert();
        expert.setUsername(request.getUsername());
        expert.setFirstName(request.getFirstName());
        expert.setLastName(request.getLastName());
        expert.setEmail(request.getEmail());
        expert.setPassword(passwordEncoder.encode(request.getPassword()));
        expert.setUserType(UserType.EXPERT);
        expert.setAge(request.getAge());
        expert.setDateOfBirth(request.getDateOfBirth());
        expert.setLatitude(request.getLatitude());
        expert.setLongitude(request.getLongitude());
        expert.setPhoneNumber(request.getPhoneNumber());
        expert.setAvatarUrl(request.getAvatarUrl());
        expert.setSpecialization(request.getSpecialization());

        Expert savedExpert = expertRepository.save(expert);

        // Créer un token de vérification email et envoyer l'email de bienvenue
        EmailVerificationToken verificationToken = emailVerificationService.createVerificationToken(savedExpert);
        emailService.sendWelcomeEmail(
            savedExpert.getEmail(),
            savedExpert.getFirstName(),
            savedExpert.getLastName(),
            savedExpert.getUserType(),
            verificationToken.getToken()
        );

        return savedExpert;
    }

    public List<UserProfileResponse> getAllExperts() {
        return expertRepository.findAll().stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
    }

    public UserProfileResponse getExpertById(Long id) {
        Expert expert = expertRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Expert avec l'ID " + id + " non trouvé"));
        return convertToProfileResponse(expert);
    }

    public UserProfileResponse getExpertByUsername(String username) {
        Expert expert = expertRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Expert '" + username + "' non trouvé"));
        return convertToProfileResponse(expert);
    }

    @Transactional
    public UserProfileResponse updateExpert(Long id, UpdateUserRequest request) {
        Expert expert = expertRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Expert avec l'ID " + id + " non trouvé"));

        // Vérifier si l'email existe déjà pour un autre utilisateur
        if (!expert.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("L'email '" + request.getEmail() + "' existe déjà");
        }

        expert.setFirstName(request.getFirstName());
        expert.setLastName(request.getLastName());
        expert.setEmail(request.getEmail());

        Expert updatedExpert = expertRepository.save(expert);
        return convertToProfileResponse(updatedExpert);
    }

    @Transactional
    public void deleteExpert(Long id) {
        Expert expert = expertRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Expert avec l'ID " + id + " non trouvé"));
        expertRepository.delete(expert);
    }

    private UserProfileResponse convertToProfileResponse(Expert expert) {
        return UserProfileResponse.builder()
                .id(expert.getId())
                .username(expert.getUsername())
                .firstName(expert.getFirstName())
                .lastName(expert.getLastName())
                .email(expert.getEmail())
                .userType(expert.getUserType())
                .age(expert.getAge())
                .dateOfBirth(expert.getDateOfBirth())
                .latitude(expert.getLatitude())
                .longitude(expert.getLongitude())
                .phoneNumber(expert.getPhoneNumber())
                .avatarUrl(expert.getAvatarUrl())
                .specialization(expert.getSpecialization())
                .userVerified(expert.isUserVerified())
                .createdAt(expert.getCreatedAt())
                .updatedAt(expert.getUpdatedAt())
                .build();
    }
}
