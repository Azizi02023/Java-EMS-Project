package com.example.demo.controller.api;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee API", description = "Endpoints for managing employees with DTOs and Soft Delete")
public class EmployeeRestController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieves a paginated list of non-deleted employees")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'USER')")
    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        return employeeService.getAllEmployees(pageable);
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore employee", description = "Restores a soft-deleted employee record")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> restoreEmployee(@PathVariable Long id) {
        employeeService.restoreEmployee(id);
        return ResponseEntity.ok("Employee restored successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete employee", description = "Marks an employee as deleted without removing from DB")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.noContent().build();
    }
}