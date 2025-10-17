package com.ciberspring.api.employee_mgmt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.ciberspring.api.employee_mgmt.entity.Employee;
import com.ciberspring.api.employee_mgmt.service.EmployeeService;
import com.ciberspring.api.employee_mgmt.service.impl.GooglePlacesService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final GooglePlacesService googlePlacesService;

    public EmployeeController(EmployeeService employeeService, GooglePlacesService googlePlacesService) {
        this.employeeService = employeeService;
        this.googlePlacesService = googlePlacesService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Map<String, String> employeeData) {
        try {
            System.out.println("=== EMPLOYEE CONTROLLER - CREATE EMPLOYEE ===");
            System.out.println("Received employee data: " + employeeData);
            
            // Create employee entity from request data
            Employee employee = new Employee();
            employee.setFirstName(employeeData.get("firstName"));
            employee.setLastName(employeeData.get("lastName"));
            employee.setEmail(employeeData.get("email"));
            employee.setLogin(employeeData.get("email")); // Use email as login
           // employee.setDob(employeeData.get("dob"));
            employee.setHomeAddress(employeeData.get("homeAddress"));
            employee.setWorkAddress(employeeData.get("workAddress"));
            employee.setPermanentAddress(employeeData.get("permanentAddress"));
            
            System.out.println("Employee object created: " + employee);
            
            Employee savedEmployee = employeeService.createEmployee(employee);
            
            System.out.println("Employee saved successfully: " + savedEmployee.getId());
            
            return ResponseEntity.ok(Map.of(
                "message", "Employee created successfully",
                "employeeId", savedEmployee.getId(),
                "email", savedEmployee.getEmail()
            ));
            
        } catch (Exception e) {
            System.out.println("=== ERROR IN EMPLOYEE CONTROLLER ===");
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
            
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create employee: " + e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(@AuthenticationPrincipal Jwt jwt) {
        System.out.println("=== JWT TOKEN DEBUG ===");
        jwt.getClaims().forEach((key, value) -> System.out.println(key + ": " + value));
        
        List<String> groups = jwt.getClaimAsStringList("groups");
        System.out.println("Groups: " + groups);
        System.out.println("Has HR_EMPLOYEES_ACCESS: " + (groups != null && groups.contains("HR_EMPLOYEES_ACCESS")));
        System.out.println("=========================");

        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/address/predictions")
    public String getAddressPredictions(@RequestParam String input) {
        return googlePlacesService.getPlacePredictions(input);
    }

    @GetMapping("/address/details")
    public String getAddressDetails(@RequestParam String placeId) {
        return googlePlacesService.getPlaceDetails(placeId);
    }
}