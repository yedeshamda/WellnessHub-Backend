package org.wellnesshubbackend.wellnesshubbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wellnesshubbackend.wellnesshubbackend.dto.UpdateUserRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UserProfileResponse;
import org.wellnesshubbackend.wellnesshubbackend.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllEmployees() {
        List<UserProfileResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getEmployeeById(@PathVariable Long id) {
        UserProfileResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileResponse> getEmployeeByUsername(@PathVariable String username) {
        UserProfileResponse employee = employeeService.getEmployeeByUsername(username);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserProfileResponse updatedEmployee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
