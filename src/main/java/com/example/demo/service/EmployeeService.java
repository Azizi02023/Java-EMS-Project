package com.example.demo.service;

import com.example.demo.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
    Page<EmployeeDTO> getAllEmployees(Pageable pageable);
    EmployeeDTO getEmployeeById(Long id);
    void saveEmployee(EmployeeDTO dto, MultipartFile imageFile, MultipartFile docFile);
    void deleteEmployeeById(Long id);
    void restoreEmployee(Long id);
    Page<EmployeeDTO> searchEmployees(String keyword, Pageable pageable);
}