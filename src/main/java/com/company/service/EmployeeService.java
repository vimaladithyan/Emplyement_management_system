package com.company.service;

import com.company.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee addEmployee(Employee employee);
    Optional<Employee> findById(Long id);
    List<Employee> findAll();
    Optional<Employee> updateEmployee(Long id, String name, String email, String department, double salary);
    boolean deleteEmployee(Long id);
    void sendSmsWelcome(String message);
    void rollbackDemo();
}
