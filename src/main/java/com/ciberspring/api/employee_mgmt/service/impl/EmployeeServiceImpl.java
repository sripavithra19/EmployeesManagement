package com.ciberspring.api.employee_mgmt.service.impl;

import org.springframework.stereotype.Service;
import com.ciberspring.api.employee_mgmt.entity.Employee;
import com.ciberspring.api.employee_mgmt.repository.EmployeeRepository;
import com.ciberspring.api.employee_mgmt.service.EmployeeService;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}