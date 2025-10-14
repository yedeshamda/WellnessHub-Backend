package org.wellnesshubbackend.wellnesshubbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wellnesshubbackend.wellnesshubbackend.model.User;
import org.wellnesshubbackend.wellnesshubbackend.model.Feedback;
import org.wellnesshubbackend.wellnesshubbackend.service.UserService;
import org.wellnesshubbackend.wellnesshubbackend.service.FeedbackService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userService;
    private final FeedbackService FeedbackService;

    // --- User endpoints ---
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // --- Feedback endpoints ---
    @PostMapping("/users/{userId}/Feedbacks")
    public Feedback createFeedback(@PathVariable Long userId, @RequestBody Feedback Feedback) {
        return FeedbackService.createFeedback(userId, Feedback);
    }

    @GetMapping("/Feedbacks")
    public List<Feedback> getAllFeedbacks() {
        return FeedbackService.getAllFeedbacks();
    }
}
