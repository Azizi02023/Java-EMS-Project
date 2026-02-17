package com.example.demo.repository;

import com.example.demo.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 1. Search method for filtering
    Page<Employee> findByFirstNameContainingOrEmailContaining(String name, String email, Pageable pageable);

    // 2. Native query to restore a soft-deleted record (bypasses @Where)
    @Modifying
    @Query(value = "UPDATE employees SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}