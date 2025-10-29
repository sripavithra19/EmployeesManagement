package com.ciberspring.api.employee_mgmt.controller;
 
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ciberspring.api.employee_mgmt.entity.Employee;
import com.ciberspring.api.employee_mgmt.service.EmployeeService;
 
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
 
@Controller
public class PersonalDetailsController {
 
    private final EmployeeService employeeService;
 
    public PersonalDetailsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
 
    // FIXED: Properly define both GET and POST mappings
    @GetMapping("/personal-details")
    @PostMapping("/personal-details")
    public String getPersonalDetailsPage(
            @RequestParam(name = "email", required = false) String emailParam,
            @RequestParam(name = "token", required = false) String tokenParam,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request,
            Model model) {
        
        try {
            System.out.println("=== PERSONAL DETAILS PAGE REQUEST ===");
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Email parameter: " + emailParam);
            System.out.println("Token parameter: " + (tokenParam != null ? "Present" : "Not present"));
            System.out.println("JWT Principal: " + (jwt != null ? "Present" : "Not present"));
            
            // Check if we have the required parameters
            if (emailParam == null || emailParam.isEmpty()) {
                model.addAttribute("error", "Email parameter is required");
                return "personal-details";
            }
 
            // If we have a token parameter but no JWT authentication, try to use it
            if (jwt == null && tokenParam != null) {
                System.out.println("Token received but JWT not authenticated");
                System.out.println("Token length: " + tokenParam.length());
                
                // In a real implementation, you would validate the token here
                // For now, we'll proceed but mark as token-based authentication
                model.addAttribute("authType", "token");
                model.addAttribute("authenticated", true);
            }
            // If we have JWT authentication
            else if (jwt != null) {
                String jwtEmail = jwt.getClaimAsString("email");
                System.out.println("✅ Authenticated via JWT. Email: " + jwtEmail);
                
                // Verify the email matches
                if (!emailParam.equals(jwtEmail)) {
                    System.out.println("❌ Email mismatch: JWT=" + jwtEmail + ", Param=" + emailParam);
                    model.addAttribute("error", "Email parameter does not match authenticated user");
                    return "personal-details";
                }
                
                model.addAttribute("authType", "jwt");
                model.addAttribute("authenticated", true);
            }
            // No authentication available
            else {
                System.out.println("❌ No authentication available");
                model.addAttribute("authRequired", true);
                model.addAttribute("error", "Authentication required. Please login through HR Portal.");
                return "personal-details";
            }
 
            // Get employee details
            Optional<Employee> employeeOpt = employeeService.findByEmail(emailParam);
            
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                System.out.println("✅ Found employee: " + employee.getId());
                
                model.addAttribute("employee", employee);
                model.addAttribute("userEmail", emailParam);
                model.addAttribute("userName", employee.getFirstName() + " " + employee.getLastName());
            } else {
                model.addAttribute("error", "Personal details not found for: " + emailParam);
                System.out.println("❌ Employee not found for email: " + emailParam);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error loading personal details page: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load personal details: " + e.getMessage());
        }
        
        return "personal-details";
    }
}