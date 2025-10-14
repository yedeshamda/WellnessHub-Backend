package org.wellnesshubbackend.wellnesshubbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EMPLOYEES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends User {

    private String employeeNumber;

}

