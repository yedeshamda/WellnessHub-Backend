package org.wellnesshubbackend.wellnesshubbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.wellnesshubbackend.wellnesshubbackend.controller.EmployeeController;
import org.wellnesshubbackend.wellnesshubbackend.dto.UpdateUserRequest;
import org.wellnesshubbackend.wellnesshubbackend.dto.UserProfileResponse;
import org.wellnesshubbackend.wellnesshubbackend.model.UserType;
import org.wellnesshubbackend.wellnesshubbackend.service.EmployeeService;
import org.wellnesshubbackend.wellnesshubbackend.utils.JwtUtils;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.*;
import org.springframework.http.MediaType;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private EmployeeController employeeController;

    private UserProfileResponse employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = UserProfileResponse.builder()
                .id(1L)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void testGetAllEmployees() {
        // Arrange
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        // Act
        List<UserProfileResponse> result = employeeController.getAllEmployees().getBody();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());

        // Verify interactions
        verify(employeeService, times(1)).getAllEmployees();
    }
    @Test
    void testGetEmployeeById() {
        UserProfileResponse employee = new UserProfileResponse();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@example.com");
        employee.setUserType(UserType.EMPLOYEE);

        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        ResponseEntity<UserProfileResponse> response = employeeController.getEmployeeById(1L);

        assertNotNull(response);
        assertEquals("John", response.getBody().getFirstName());
        verify(employeeService, times(1)).getEmployeeById(1L);
    }
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testUpdateEmployeeWithoutMockMvc() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        UserProfileResponse updated = new UserProfileResponse();
        updated.setId(1L);
        updated.setUsername("john");
        updated.setFirstName("John");
        updated.setLastName("Doe");
        updated.setUserType(UserType.EMPLOYEE);

        when(employeeService.updateEmployee(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(updated);

        ResponseEntity<UserProfileResponse> response = employeeController.updateEmployee(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("john", response.getBody().getUsername());
        assertEquals(UserType.EMPLOYEE, response.getBody().getUserType());
    }
    @Test
    void testDeleteEmployeeWithoutMockMvc() {
        // Arrange: do nothing when deleteEmployee is called
        doNothing().when(employeeService).deleteEmployee(1L);

        // Act: call the controller method directly
        ResponseEntity<Void> response = employeeController.deleteEmployee(1L);

        // Assert: check the response status
        assertEquals(204, response.getStatusCodeValue());

        // Verify: ensure the service was called
        verify(employeeService, times(1)).deleteEmployee(1L);
    }

}
