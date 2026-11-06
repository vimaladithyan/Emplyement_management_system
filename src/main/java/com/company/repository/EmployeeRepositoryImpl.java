package com.company.repository;

import com.company.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository layer (PRD §5.3): persistence only, no business rules.
 * Uses the shared (transaction-bound) EntityManager via @PersistenceContext —
 * it participates in the transaction started by the service layer.
 */
@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Employee save(Employee employee) {
        entityManager.persist(employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Employee.class, id));
    }

    @Override
    public List<Employee> findAll() {
        return entityManager.createQuery("SELECT e FROM Employee e", Employee.class)
                .getResultList();
    }

    @Override
    public Employee update(Employee employee) {
        return entityManager.merge(employee);
    }

    @Override
    public void delete(Employee employee) {
        entityManager.remove(employee);
    }
}
