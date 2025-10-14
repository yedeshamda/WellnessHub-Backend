package org.wellnesshubbackend.wellnesshubbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EXPERTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expert extends User {

    private String specialization;

}