package org.wellnesshubbackend.wellnesshubbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HR_PERSONNEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HrPersonnel extends User {

    private String companyName;

    public HrPersonnel(String username, String email, String password) {
        super(username, email, password); // appelle le constructeur du parent
    }

}


