package com.company;

import com.company.config.AppConfig;
import com.company.entity.Employee;
import com.company.service.EmployeeService;
import com.company.util.EmployeeIdGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Scanner;

/**
 * Presentation layer (PRD §5.1): console menu only.
 * No SQL, no business logic — input collection, service calls, result display.
 * Starts via AnnotationConfigApplicationContext (no Boot / MVC / server).
 */
public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        EmployeeService service = context.getBean(EmployeeService.class);
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Employee Management System (Spring Core + JPA) ===");
        System.out.println("App: " + context.getBean("applicationName"));

        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Register employee");
            System.out.println("2. Find employee by ID");
            System.out.println("3. List all employees");
            System.out.println("4. Update employee");
            System.out.println("5. Delete employee");
            System.out.println("6. Rollback demo (tx must NOT commit)");
            System.out.println("7. Send via SMS (@Qualifier demo)");
            System.out.println("8. Scope demo (singleton vs prototype)");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> {
                        System.out.print("Name: "); String name = scanner.nextLine();
                        System.out.print("Email: "); String email = scanner.nextLine();
                        System.out.print("Department: "); String dept = scanner.nextLine();
                        System.out.print("Salary: "); double salary = Double.parseDouble(scanner.nextLine());
                        Employee saved = service.addEmployee(new Employee(name, email, dept, salary));
                        System.out.println("Registered: " + saved);
                    }
                    case 2 -> {
                        System.out.print("Employee ID: "); Long id = Long.parseLong(scanner.nextLine());
                        service.findById(id).ifPresentOrElse(
                                System.out::println,
                                () -> System.out.println("Employee not found for ID=" + id));
                    }
                    case 3 -> {
                        List<Employee> all = service.findAll();
                        if (all.isEmpty()) {
                            System.out.println("No employees found.");
                        } else {
                            all.forEach(System.out::println);
                        }
                    }
                    case 4 -> {
                        System.out.print("Employee ID: "); Long id = Long.parseLong(scanner.nextLine());
                        System.out.print("New name: "); String name = scanner.nextLine();
                        System.out.print("New email: "); String email = scanner.nextLine();
                        System.out.print("New department: "); String dept = scanner.nextLine();
                        System.out.print("New salary: "); double salary = Double.parseDouble(scanner.nextLine());
                        service.updateEmployee(id, name, email, dept, salary).ifPresentOrElse(
                                e -> System.out.println("Updated: " + e),
                                () -> System.out.println("Employee not found for ID=" + id));
                    }
                    case 5 -> {
                        System.out.print("Employee ID: "); Long id = Long.parseLong(scanner.nextLine());
                        System.out.println(service.deleteEmployee(id)
                                ? "Deleted ID=" + id : "Employee not found for ID=" + id);
                    }
                    case 6 -> {
                        try {
                            service.rollbackDemo();
                        } catch (RuntimeException ex) {
                            System.out.println("Caught (expected): " + ex.getMessage());
                            System.out.println("Transaction rolled back — no 'Rollback' row should exist.");
                        }
                    }
                    case 7 -> service.sendSmsWelcome("Welcome aboard (sent via SmsNotificationService @Qualifier)");
                    case 8 -> {
                        EmployeeService a = context.getBean(EmployeeService.class);
                        EmployeeService b = context.getBean(EmployeeService.class);
                        EmployeeIdGenerator g1 = context.getBean(EmployeeIdGenerator.class);
                        EmployeeIdGenerator g2 = context.getBean(EmployeeIdGenerator.class);
                        System.out.println("Singleton EmployeeService same instance? " + (a == b) + " (expected true)");
                        System.out.println("Prototype EmployeeIdGenerator same instance? " + (g1 == g2) + " (expected false)");
                    }
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            } catch (RuntimeException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }

        context.close();
        System.out.println("Application closed — @PreDestroy callbacks executed.");
    }
}
