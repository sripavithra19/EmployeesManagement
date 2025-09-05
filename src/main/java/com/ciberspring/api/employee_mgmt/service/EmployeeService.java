package com.ciberspring.api.employee_mgmt.service;

import com.ciberspring.api.employee_mgmt.entity.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id);
}