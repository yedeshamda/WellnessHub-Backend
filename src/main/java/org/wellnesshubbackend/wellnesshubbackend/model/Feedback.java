package org.wellnesshubbackend.wellnesshubbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FEEDBACK")
@Getter
@Setter
@NoArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sujet;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Feedback(String sujet, String description, User user) {
        this.sujet = sujet;
        this.description = description;
        this.user = user;
    }
}
