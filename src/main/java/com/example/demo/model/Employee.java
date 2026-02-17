package com.example.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee Entity representing the 'employees' table.
 * Includes Soft Delete logic and File Storage fields.
 */
@Data
@Entity
@Table(name = "employees")
// 1. Convert any 'delete' call into an 'update' that sets deleted=true
@SQLDelete(sql = "UPDATE employees SET deleted = true WHERE id=?")
// 2. Automatically filter out records where deleted=true in all select queries
@Where(clause = "deleted=false")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Soft Delete Flag ---
    @Column(nullable = false)
    private boolean deleted = false;

    // --- Core Information ---
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 2, message = "First name must have at least 2 characters")
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @Column(name = "last_name")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Column(unique = true)
    private String email;

    private String phone;
    private String department;
    private String position;

    @PositiveOrZero(message = "Salary must be a positive value")
    private BigDecimal salary;

    @NotNull(message = "Hire date cannot be null")
    @Column(name = "hire_date")
    private LocalDate hireDate;

    // --- DATABASE FIELDS (Stores the file paths/names) ---
    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "document")
    private String document;

    // --- FORM FIELDS (Transient - These are NOT stored in the database) ---
    @Transient
    private MultipartFile imageFile;

    @Transient
    private MultipartFile docFile;
}