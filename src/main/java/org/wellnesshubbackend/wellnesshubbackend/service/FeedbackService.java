
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
public class FeedbackService {
    private final FeedbackRepository FeedbackRepository;
    private final UserRepository userRepository;

    public Feedback createFeedback(Long userId, Feedback Feedback) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Feedback.setUser(user);
            return FeedbackRepository.save(Feedback);
        }
        return null;
    }

    public List<Feedback> getAllFeedbacks() {
        return FeedbackRepository.findAll();
    }
}