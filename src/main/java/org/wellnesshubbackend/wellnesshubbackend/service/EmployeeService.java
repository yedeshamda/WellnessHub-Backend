package org.wellnesshubbackend.wellnesshubbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wellnesshubbackend.wellnesshubbackend.dto.RegisterEmployeeRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UpdateUserRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UserProfileResponse;
import org.wellnesshubbackend.wellnesshubbackend.exception.UserAlreadyExistsException;
import org.wellnesshubbackend.wellnesshubbackend.exception.UserNotFoundException;
import org.wellnesshubbackend.wellnesshubbackend.model.EmailVerificationToken;
import org.wellnesshubbackend.wellnesshubbackend.model.Employee;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;
import org.wellnesshubbackend.wellnesshubbackend.repository.EmployeeRepository;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public Employee registerEmployee(RegisterEmployeeRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Le nom d'utilisateur '" + request.getUsername() + "' existe déjà");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("L'email '" + request.getEmail() + "' existe déjà");
        }

        Employee employee = new Employee();
        employee.setUsername(request.getUsername());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setUserType(UserType.EMPLOYEE);
        employee.setAge(request.getAge());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setLatitude(request.getLatitude());
        employee.setLongitude(request.getLongitude());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setAvatarUrl(request.getAvatarUrl());
        employee.setEmployeeNumber(request.getEmployeeNumber());

        Employee savedEmployee = employeeRepository.save(employee);

        // Créer un token de vérification email et envoyer l'email de bienvenue
        EmailVerificationToken verificationToken = emailVerificationService.createVerificationToken(savedEmployee);
        emailService.sendWelcomeEmail(
            savedEmployee.getEmail(),
            savedEmployee.getFirstName(),
            savedEmployee.getLastName(),
            savedEmployee.getUserType(),
            verificationToken.getToken()
        );

        return savedEmployee;
    }

    public List<UserProfileResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
    }

    public UserProfileResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Employee avec l'ID " + id + " non trouvé"));
        return convertToProfileResponse(employee);
    }

    public UserProfileResponse getEmployeeByUsername(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Employee '" + username + "' non trouvé"));
        return convertToProfileResponse(employee);
    }

    @Transactional
    public UserProfileResponse updateEmployee(Long id, UpdateUserRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Employee avec l'ID " + id + " non trouvé"));

        // Vérifier si l'email existe déjà pour un autre utilisateur
        if (!employee.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("L'email '" + request.getEmail() + "' existe déjà");
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());

        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToProfileResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Employee avec l'ID " + id + " non trouvé"));
        employeeRepository.delete(employee);
    }

    private UserProfileResponse convertToProfileResponse(Employee employee) {
        return UserProfileResponse.builder()
                .id(employee.getId())
                .username(employee.getUsername())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .userType(employee.getUserType())
                .age(employee.getAge())
                .dateOfBirth(employee.getDateOfBirth())
                .latitude(employee.getLatitude())
                .longitude(employee.getLongitude())
                .phoneNumber(employee.getPhoneNumber())
                .avatarUrl(employee.getAvatarUrl())
                .employeeNumber(employee.getEmployeeNumber())
                .userVerified(employee.isUserVerified())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
