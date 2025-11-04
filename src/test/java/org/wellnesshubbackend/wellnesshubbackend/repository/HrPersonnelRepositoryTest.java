package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.wellnesshubbackend.wellnesshubbackend.model.HrPersonnel;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
@DataJpaTest
public class HrPersonnelRepositoryTest {

    @Autowired
    private HrPersonnelRepository hrPersonnelRepository;

    @Test
    void testFindByUsernameAndEmail() {
        // GIVEN
        HrPersonnel hr = new HrPersonnel("nour", "nour@example.com", "password123");
        hr.setUserType(UserType.HR_PERSONNEL);
        hrPersonnelRepository.save(hr);

        // WHEN
        Optional<HrPersonnel> foundByUsername = hrPersonnelRepository.findByUsername("nour");
        Optional<HrPersonnel> foundByEmail = hrPersonnelRepository.findByEmail("nour@example.com");

        // THEN
        assertThat(foundByUsername).isPresent();
        assertThat(foundByEmail).isPresent();
        assertThat(foundByUsername.get().getEmail()).isEqualTo("nour@example.com");
    }
}
