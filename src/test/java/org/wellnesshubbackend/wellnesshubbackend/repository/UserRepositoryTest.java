package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.wellnesshubbackend.wellnesshubbackend.model.User;

import java.util.List;

@SpringBootTest

public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Transactional
    @Test
    void whenSaveUsers_thenFindAllReturnsThem() {
        // Create and save users
        User u1 = new User( "john_doe", "john@example.com");
        User u2 = new User( "jane_doe", "jane@example.com");

        userRepository.save(u1);
        userRepository.save(u2);

        // Fetch all users
        List<User> users = userRepository.findAll();

        Assertions.assertEquals(2, users.size());
    }
}
