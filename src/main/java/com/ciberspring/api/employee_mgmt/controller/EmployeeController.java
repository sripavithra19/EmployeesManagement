package com.ciberspring.api.employee_mgmt.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciberspring.api.employee_mgmt.entity.Employee;
import com.ciberspring.api.employee_mgmt.service.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

	/*
	 * @GetMapping public List<Employee> getAllEmployees() { return
	 * employeeService.getAllEmployees(); }
	 */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); 
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(@AuthenticationPrincipal Jwt jwt) {
        // DEBUG: Print all claims in the token
        System.out.println("=== JWT TOKEN DEBUG ===");
        jwt.getClaims().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
        
        // Check specifically for groups
        List<String> groups = jwt.getClaimAsStringList("groups");
        System.out.println("Groups: " + groups);
        System.out.println("Has HR_EMPLOYEES_ACCESS: " + (groups != null && groups.contains("HR_EMPLOYEES_ACCESS")));
        System.out.println("=========================");
        
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }
}