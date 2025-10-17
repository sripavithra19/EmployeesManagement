package com.ciberspring.api.employee_mgmt.service;

import com.ciberspring.api.employee_mgmt.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id); // Change to String
    Employee createEmployee(Employee employee);
    Employee updateEmployee(Employee employee);
    void deleteEmployee(int id); // Change to String
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByLogin(String login);
    boolean existsByLogin(String login);
}