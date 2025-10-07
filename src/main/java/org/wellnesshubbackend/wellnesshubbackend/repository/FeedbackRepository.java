package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wellnesshubbackend.wellnesshubbackend.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
