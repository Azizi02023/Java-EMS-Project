package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction; // Use this instead of org.hibernate.annotations.Where
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@SQLDelete(sql = "UPDATE employees SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false") // This replaces @Where(clause = "deleted = false")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private BigDecimal salary;
    private LocalDate hireDate;
    private String profileImage;
    private String document;

    @Column(nullable = false)
    private boolean deleted = false;
}