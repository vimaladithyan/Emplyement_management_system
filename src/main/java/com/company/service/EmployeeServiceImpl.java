package com.company.service;

import com.company.entity.Employee;
import com.company.notification.NotificationService;
import com.company.notification.SmsNotificationService;
import com.company.repository.EmployeeRepository;
import com.company.util.EmployeeIdGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer (PRD §5.2): business logic + transaction boundaries.
 * Constructor injection for all mandatory dependencies (PRD §9).
 * <p>
 * Demonstrates: @Autowired (constructor), @Primary (NotificationService →
 * EmailNotificationService), @Qualifier (explicit SmsNotificationService),
 * @Transactional(@Transactional(readOnly=true) for reads) — unchecked
 * exceptions roll back by Spring's default rule.
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService; // @Primary -> email
    private final SmsNotificationService smsNotificationService; // explicit bean
    private final EmployeeIdGenerator employeeIdGenerator;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               NotificationService notificationService,
                               @Qualifier("smsNotificationService")
                               SmsNotificationService smsNotificationService,
                               EmployeeIdGenerator employeeIdGenerator) {
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.smsNotificationService = smsNotificationService;
        this.employeeIdGenerator = employeeIdGenerator;
    }

    @PostConstruct
    public void init() {
        System.out.println("[EmployeeServiceImpl] Initialised (@PostConstruct)");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("[EmployeeServiceImpl] Destroyed (@PreDestroy)");
    }

    // --- 4.1 Register -------------------------------------------------------
    @Override
    @Transactional
    public Employee addEmployee(Employee employee) {
        validate(employee);
        String ref = employeeIdGenerator.generateReferenceCode();
        employeeRepository.save(employee);
        notificationService.sendNotification(
                "Employee registered: " + employee.getName() + " (" + employee.getEmail() + ") ref=" + ref);
        return employee;
    }

    // --- 4.2 By ID ----------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    // --- 4.3 All ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // --- 4.4 Update ---------------------------------------------------------
    @Override
    @Transactional
    public Optional<Employee> updateEmployee(Long id, String name, String email, String department, double salary) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        Employee employee = existing.get();
        employee.setName(name);
        employee.setEmail(email);
        employee.setDepartment(department);
        employee.setSalary(salary);
        validate(employee);
        return Optional.of(employeeRepository.update(employee));
    }

    // --- 4.5 Delete ---------------------------------------------------------
    @Override
    @Transactional
    public boolean deleteEmployee(Long id) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        employeeRepository.delete(existing.get());
        return true;
    }

    // --- 4.6 Explicit SMS via @Qualifier-selected bean ----------------------
    @Override
    public void sendSmsWelcome(String message) {
        smsNotificationService.sendNotification(message);
    }

    // --- §8 Rollback demo: persist, then unchecked exception -> rollback ---
    @Override
    @Transactional
    public void rollbackDemo() {
        employeeRepository.save(new Employee("Rollback", "rollback@company.local", "Demo", 1.0));
        throw new IllegalStateException("Intentional unchecked exception -> transaction must roll back");
    }

    private void validate(Employee employee) {
        if (employee.getName() == null || employee.getName().isBlank()) {
            throw new IllegalArgumentException("Employee name is required");
        }
        if (employee.getEmail() == null || !employee.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (employee.getDepartment() == null || employee.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (employee.getSalary() <= 0) {
            throw new IllegalArgumentException("Salary must be > 0");
        }
    }
}
