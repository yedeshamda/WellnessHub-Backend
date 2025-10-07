package org.wellnesshubbackend.wellnesshubbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wellnesshubbackend.wellnesshubbackend.model.Feedback;
import org.wellnesshubbackend.wellnesshubbackend.model.User;
import org.wellnesshubbackend.wellnesshubbackend.repository.FeedbackRepository;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}