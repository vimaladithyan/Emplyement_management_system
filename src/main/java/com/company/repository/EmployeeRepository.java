package com.company.repository;

import com.company.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(Long id);
    List<Employee> findAll();
    Employee update(Employee employee);
    void delete(Employee employee);
}
