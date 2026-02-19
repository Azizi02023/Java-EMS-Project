package com.example.demo.repository;

import com.example.demo.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // FIXED: This method name now matches the call in EmployeeServiceImpl
    Page<Employee> findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String email, Pageable pageable);

    // Advanced filtering by name, email, and department
    @Query("SELECT e FROM Employee e WHERE " +
            "(:keyword IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:dept IS NULL OR e.department = :dept)")
    Page<Employee> findWithFilters(@Param("keyword") String keyword, @Param("dept") String dept, Pageable pageable);

    @Modifying
    @Query(value = "UPDATE employees SET deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}