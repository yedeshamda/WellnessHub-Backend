package org.wellnesshubbackend.wellnesshubbackend.services;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wellnesshubbackend.wellnesshubbackend.model.User;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;
import org.wellnesshubbackend.wellnesshubbackend.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserService userService; // Classe que l'on teste

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialise les mocks
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setUserType(UserType.EMPLOYEE);

        when(userRepository.save(user)).thenReturn(user); // Simule le save

        User created = userService.createUser(user);
        assertNotNull(created);
        assertEquals("john_doe", created.getUsername());
        verify(userRepository, times(1)).save(user); // Vérifie que save a été appelé
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User();
        u1.setUsername("john_doe");

        User u2 = new User();
        u2.setUsername("jane_doe");

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("john_doe")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("jane_doe")));
    }
}
