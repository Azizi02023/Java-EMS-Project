package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "date"})
        }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private com.example.demo.model.Employee employee; // Simplified name

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    // Add this annotation to your date fields
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.example.demo.model.AttendanceStatus status; // This should now resolve

    private String remarks;

    // FIX: The previous version had an empty body {} which caused data loss.
    // Also simplified the parameter type.
    public void setStatus(com.example.demo.model.AttendanceStatus status) {
        this.status = status;
    }
}