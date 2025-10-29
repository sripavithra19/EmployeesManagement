package com.ciberspring.api.employee_mgmt.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ciberspring.api.employee_mgmt.entity.Employee;
import com.ciberspring.api.employee_mgmt.repository.EmployeeRepository;
import com.ciberspring.api.employee_mgmt.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${okta.api.url:https://trial-6355671.okta.com}")
    private String oktaApiUrl;

    @Value("${okta.api.token:}")
    private String oktaApiToken;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public Employee createEmployee(Employee employee) {
        try {
            System.out.println("=== EMPLOYEE SERVICE - CREATE EMPLOYEE ===");
            System.out.println("Employee to save: " + employee);
            
            // Set login to email if not provided
            if (employee.getLogin() == null && employee.getEmail() != null) {
                employee.setLogin(employee.getEmail());
            }
            
            // Validate required fields
            if (employee.getLogin() == null || employee.getLogin().trim().isEmpty()) {
                throw new RuntimeException("Login is required for employee");
            }
            
            // Check if login already exists
            boolean loginExists = employeeRepository.existsByLogin(employee.getLogin());
            System.out.println("Login exists check: " + loginExists);
            
            if (loginExists) {
                throw new RuntimeException("Employee with login '" + employee.getLogin() + "' already exists");
            }
            
            // DO NOT set ID manually - let database generate it
            // Remove any employee.setId() calls
            
            // Save to database - ID will be auto-generated
            System.out.println("Saving employee to database (ID will be auto-generated)...");
            Employee savedEmployee = employeeRepository.save(employee);
            System.out.println("Employee saved with AUTO-GENERATED ID: " + savedEmployee.getId());
            
            return savedEmployee;
            
        } catch (Exception e) {
            System.out.println("=== ERROR IN EMPLOYEE SERVICE ===");
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
            throw new RuntimeException("Failed to create employee: " + e.getMessage(), e);
        }
    }

    private void createOktaUser(Employee employee) {
        try {
            String url = oktaApiUrl + "/api/v1/users?activate=true";
            
            // Prepare Okta user creation payload
            Map<String, Object> profile = new HashMap<>();
            profile.put("firstName", employee.getFirstName());
            profile.put("lastName", employee.getLastName());
            profile.put("email", employee.getEmail());
            profile.put("login", employee.getLogin());
            
            Map<String, Object> oktaUser = new HashMap<>();
            oktaUser.put("profile", profile);
            
            String requestBody = objectMapper.writeValueAsString(oktaUser);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "SSWS " + oktaApiToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Successfully created Okta user for: " + employee.getEmail());
                
                // Parse response to get Okta user ID if needed
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                String oktaUserId = (String) responseMap.get("id");
                System.out.println("Okta User ID: " + oktaUserId);
                
            } else {
                System.err.println("Failed to create Okta user. Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("Error creating Okta user for " + employee.getEmail() + ": " + e.getMessage());
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        if (!employeeRepository.existsById(employee.getId())) {
            throw new RuntimeException("Employee not found with id: " + employee.getId());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(int id) {  
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public Optional<Employee> findByLogin(String login) {
        return employeeRepository.findByLogin(login);
    }

    @Override
    public boolean existsByLogin(String login) {
        return employeeRepository.existsByLogin(login);
    }
}