package org.wellnesshubbackend.wellnesshubbackend;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wellnesshubbackend.wellnesshubbackend.dto.RegisterHrPersonnelRequest;
import org.wellnesshubbackend.wellnesshubbackend.model.EmailVerificationToken;
import org.wellnesshubbackend.wellnesshubbackend.model.HrPersonnel;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;
import org.wellnesshubbackend.wellnesshubbackend.repository.HrPersonnelRepository;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;
import org.wellnesshubbackend.wellnesshubbackend.service.EmailService;
import org.wellnesshubbackend.wellnesshubbackend.service.EmailVerificationService;
import org.wellnesshubbackend.wellnesshubbackend.service.HrPersonnelService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HrPersonnelTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HrPersonnelRepository hrPersonnelRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private HrPersonnelService hrPersonnelService;

    @Test
    void testRegisterHrPersonnel() {
        // 1. Créer la requête
        RegisterHrPersonnelRequest request = new RegisterHrPersonnelRequest();
        request.setUsername("Nour");
        request.setFirstName("Nour");
        request.setLastName("Khedri");
        request.setEmail("nour@example.com");
        request.setPassword("password123");
        request.setAge(25);
        request.setPhoneNumber("+21612345678");
        request.setCompanyName("MyCompany");

        // 2. Mock des vérifications de l'utilisateur
        when(userRepository.existsByUsername("Nour")).thenReturn(false);
        when(userRepository.existsByEmail("nour@example.com")).thenReturn(false);

        // 3. Mock du token de vérification
        EmailVerificationToken fakeToken = new EmailVerificationToken();
        fakeToken.setToken("fake-token");
        when(emailVerificationService.createVerificationToken(any(HrPersonnel.class)))
                .thenReturn(fakeToken);

        // 4. Mock du repository pour sauvegarde
        HrPersonnel savedHr = new HrPersonnel();
        savedHr.setId(1L);
        savedHr.setUsername("Nour");
        savedHr.setFirstName("Nour");        // ajouter firstName
        savedHr.setLastName("Khedri");       // ajouter lastName
        savedHr.setEmail("nour@example.com");
        savedHr.setUserType(UserType.HR_PERSONNEL); // ajouter userType

        when(hrPersonnelRepository.save(any(HrPersonnel.class))).thenReturn(savedHr);

        // 5. Appeler la méthode du service
        HrPersonnel result = hrPersonnelService.registerHrPersonnel(request);

        // 6. Vérifier les résultats
        assertNotNull(result);
        assertEquals("Nour", result.getUsername());
        assertEquals("nour@example.com", result.getEmail());

        // 7. Vérifier les interactions avec les mocks
        verify(hrPersonnelRepository, times(1)).save(any(HrPersonnel.class));
        verify(emailVerificationService, times(1)).createVerificationToken(any(HrPersonnel.class));
        verify(emailService, times(1)).sendWelcomeEmail(
                eq("nour@example.com"),
                eq("Nour"),
                eq("Khedri"),
                eq(UserType.HR_PERSONNEL),
                eq("fake-token")
        );
    }
}
