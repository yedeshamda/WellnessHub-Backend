package org.wellnesshubbackend.wellnesshubbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    private String nom;
    private String prenom;
    private int age;

    public User(String username, String email, String nom, String prenom, int age) {
        this.username = username;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
    }
}
