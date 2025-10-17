package com.ciberspring.api.employee_mgmt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ciberspring.api.employee_mgmt.service.impl.GooglePlacesService;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final GooglePlacesService googlePlacesService;

    public DebugController(GooglePlacesService googlePlacesService) {
        this.googlePlacesService = googlePlacesService;
    }

    @GetMapping("/places/test")
    public String testPlacesApi(@RequestParam(required = false) String input) {
        String testInput = input != null ? input : "New York";
        return googlePlacesService.getPlacePredictions(testInput);
    }

    @GetMapping("/places/validate")
    public String validateApiKey() {
        boolean isValid = googlePlacesService.validateApiKey();
        return String.format("{\"apiKeyValid\": %s}", isValid);
    }
}