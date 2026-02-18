package com.example.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction; // Change this import
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "employees")
// 1. Keeps your manual Soft Delete logic
@SQLDelete(sql = "UPDATE employees SET deleted = true WHERE id=?")
// 2. REPLACEMENT for @Where: Automatically filters out deleted records
@SQLRestriction("deleted = false")
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

    // --- DATABASE FIELDS (Store Filenames) ---
    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "document")
    private String document;

    // --- FORM FIELDS (Transient - Not stored in DB) ---
    @Transient
    private MultipartFile imageFile;

    @Transient
    private MultipartFile docFile;
}