package com.example.demo.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
// Add this annotation to your date fields


@Data
public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private BigDecimal salary;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    // These store the file names in the database
    private String profileImage;
    private String document;

    // These handle the actual file upload from the HTML form
    private MultipartFile imageFile;
    private MultipartFile docFile;
}