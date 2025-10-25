package org.wellnesshubbackend.wellnesshubbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wellnesshubbackend.wellnesshubbackend.dto.UpdateUserRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UserProfileResponse;
import org.wellnesshubbackend.wellnesshubbackend.service.ExpertService;

import java.util.List;

@RestController
@RequestMapping("/api/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertService expertService;



    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllExperts() {
        List<UserProfileResponse> experts = expertService.getAllExperts();
        return ResponseEntity.ok(experts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getExpertById(@PathVariable Long id) {
        UserProfileResponse expert = expertService.getExpertById(id);
        return ResponseEntity.ok(expert);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileResponse> getExpertByUsername(@PathVariable String username) {
        UserProfileResponse expert = expertService.getExpertByUsername(username);
        return ResponseEntity.ok(expert);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateExpert(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserProfileResponse updatedExpert = expertService.updateExpert(id, request);
        return ResponseEntity.ok(updatedExpert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpert(@PathVariable Long id) {
        expertService.deleteExpert(id);
        return ResponseEntity.noContent().build();
    }
}
