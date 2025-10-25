package org.wellnesshubbackend.wellnesshubbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wellnesshubbackend.wellnesshubbackend.dto.RegisterHrPersonnelRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UpdateUserRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UserProfileResponse;
import org.wellnesshubbackend.wellnesshubbackend.exception.UserAlreadyExistsException;
import org.wellnesshubbackend.wellnesshubbackend.exception.UserNotFoundException;
import org.wellnesshubbackend.wellnesshubbackend.model.EmailVerificationToken;
import org.wellnesshubbackend.wellnesshubbackend.model.HrPersonnel;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;
import org.wellnesshubbackend.wellnesshubbackend.repository.HrPersonnelRepository;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrPersonnelService {

    private final HrPersonnelRepository hrPersonnelRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public HrPersonnel registerHrPersonnel(RegisterHrPersonnelRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Le nom d'utilisateur '" + request.getUsername() + "' existe déjà");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("L'email '" + request.getEmail() + "' existe déjà");
        }

        HrPersonnel hrPersonnel = new HrPersonnel();
        hrPersonnel.setUsername(request.getUsername());
        hrPersonnel.setFirstName(request.getFirstName());
        hrPersonnel.setLastName(request.getLastName());
        hrPersonnel.setEmail(request.getEmail());
        hrPersonnel.setPassword(passwordEncoder.encode(request.getPassword()));
        hrPersonnel.setUserType(UserType.HR_PERSONNEL);
        hrPersonnel.setAge(request.getAge());
        hrPersonnel.setDateOfBirth(request.getDateOfBirth());
        hrPersonnel.setLatitude(request.getLatitude());
        hrPersonnel.setLongitude(request.getLongitude());
        hrPersonnel.setPhoneNumber(request.getPhoneNumber());
        hrPersonnel.setAvatarUrl(request.getAvatarUrl());
        hrPersonnel.setCompanyName(request.getCompanyName());

        HrPersonnel savedHrPersonnel = hrPersonnelRepository.save(hrPersonnel);

        // Créer un token de vérification email et envoyer l'email de bienvenue
        EmailVerificationToken verificationToken = emailVerificationService.createVerificationToken(savedHrPersonnel);
        emailService.sendWelcomeEmail(
            savedHrPersonnel.getEmail(),
            savedHrPersonnel.getFirstName(),
            savedHrPersonnel.getLastName(),
            savedHrPersonnel.getUserType(),
            verificationToken.getToken()
        );

        return savedHrPersonnel;
    }

    public List<UserProfileResponse> getAllHrPersonnel() {
        return hrPersonnelRepository.findAll().stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
    }

    public UserProfileResponse getHrPersonnelById(Long id) {
        HrPersonnel hrPersonnel = hrPersonnelRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("HrPersonnel avec l'ID " + id + " non trouvé"));
        return convertToProfileResponse(hrPersonnel);
    }

    public UserProfileResponse getHrPersonnelByUsername(String username) {
        HrPersonnel hrPersonnel = hrPersonnelRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("HrPersonnel '" + username + "' non trouvé"));
        return convertToProfileResponse(hrPersonnel);
    }

    @Transactional
    public UserProfileResponse updateHrPersonnel(Long id, UpdateUserRequest request) {
        HrPersonnel hrPersonnel = hrPersonnelRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("HrPersonnel avec l'ID " + id + " non trouvé"));

        // Vérifier si l'email existe déjà pour un autre utilisateur
        if (!hrPersonnel.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("L'email '" + request.getEmail() + "' existe déjà");
        }

        hrPersonnel.setFirstName(request.getFirstName());
        hrPersonnel.setLastName(request.getLastName());
        hrPersonnel.setEmail(request.getEmail());

        HrPersonnel updatedHrPersonnel = hrPersonnelRepository.save(hrPersonnel);
        return convertToProfileResponse(updatedHrPersonnel);
    }

    @Transactional
    public void deleteHrPersonnel(Long id) {
        HrPersonnel hrPersonnel = hrPersonnelRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("HrPersonnel avec l'ID " + id + " non trouvé"));
        hrPersonnelRepository.delete(hrPersonnel);
    }

    private UserProfileResponse convertToProfileResponse(HrPersonnel hrPersonnel) {
        return UserProfileResponse.builder()
                .id(hrPersonnel.getId())
                .username(hrPersonnel.getUsername())
                .firstName(hrPersonnel.getFirstName())
                .lastName(hrPersonnel.getLastName())
                .email(hrPersonnel.getEmail())
                .userType(hrPersonnel.getUserType())
                .age(hrPersonnel.getAge())
                .dateOfBirth(hrPersonnel.getDateOfBirth())
                .latitude(hrPersonnel.getLatitude())
                .longitude(hrPersonnel.getLongitude())
                .phoneNumber(hrPersonnel.getPhoneNumber())
                .avatarUrl(hrPersonnel.getAvatarUrl())
                .companyName(hrPersonnel.getCompanyName())
                .userVerified(hrPersonnel.isUserVerified())
                .createdAt(hrPersonnel.getCreatedAt())
                .updatedAt(hrPersonnel.getUpdatedAt())
                .build();
    }
}
