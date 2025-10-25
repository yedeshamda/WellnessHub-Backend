package org.wellnesshubbackend.wellnesshubbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wellnesshubbackend.wellnesshubbackend.dto.UpdateUserRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UserProfileResponse;
import org.wellnesshubbackend.wellnesshubbackend.service.HrPersonnelService;

import java.util.List;

@RestController
@RequestMapping("/api/hr-personnel")
@RequiredArgsConstructor
public class HrPersonnelController {

    private final HrPersonnelService hrPersonnelService;



    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllHrPersonnel() {
        List<UserProfileResponse> hrPersonnel = hrPersonnelService.getAllHrPersonnel();
        return ResponseEntity.ok(hrPersonnel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getHrPersonnelById(@PathVariable Long id) {
        UserProfileResponse hrPersonnel = hrPersonnelService.getHrPersonnelById(id);
        return ResponseEntity.ok(hrPersonnel);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileResponse> getHrPersonnelByUsername(@PathVariable String username) {
        UserProfileResponse hrPersonnel = hrPersonnelService.getHrPersonnelByUsername(username);
        return ResponseEntity.ok(hrPersonnel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateHrPersonnel(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserProfileResponse updatedHrPersonnel = hrPersonnelService.updateHrPersonnel(id, request);
        return ResponseEntity.ok(updatedHrPersonnel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHrPersonnel(@PathVariable Long id) {
        hrPersonnelService.deleteHrPersonnel(id);
        return ResponseEntity.noContent().build();
    }
}
