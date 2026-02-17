package com.example.demo.service;

import com.example.demo.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
    // 1. Returns DTOs instead of Entities
    Page<EmployeeDTO> getAllEmployees(Pageable pageable);

    // 2. Search/Filter functionality
    Page<EmployeeDTO> searchEmployees(String keyword, Pageable pageable);

    EmployeeDTO getEmployeeById(Long id);

    // 3. Takes DTO and files for saving
    void saveEmployee(EmployeeDTO employeeDTO, MultipartFile imageFile, MultipartFile docFile);

    void deleteEmployeeById(Long id);

    // 4. Restore soft-deleted employee
    void restoreEmployee(Long id);
}