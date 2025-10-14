package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.wellnesshubbackend.wellnesshubbackend.model.User;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;

import java.util.List;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    void whenSaveUsers_thenFindAllReturnsThem() {
        // Create and save users
        User u1 = new User();
        u1.setUsername("john_doe");
        u1.setFirstName("John");
        u1.setLastName("Doe");
        u1.setEmail("john@example.com");
        u1.setPassword("password123");
        u1.setUserType(UserType.EMPLOYEE);

        User u2 = new User();
        u2.setUsername("jane_doe");
        u2.setFirstName("Jane");
        u2.setLastName("Doe");
        u2.setEmail("jane@example.com");
        u2.setPassword("password123");
        u2.setUserType(UserType.EMPLOYEE);

        userRepository.save(u1);
        userRepository.save(u2);

        // Fetch all users
        List<User> users = userRepository.findAll();

        Assertions.assertTrue(users.size() >= 2);
        Assertions.assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("john_doe")));
        Assertions.assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("jane_doe")));
    }
}
