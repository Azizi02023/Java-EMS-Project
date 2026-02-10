package com.example.demo.service;

import com.example.demo.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile; // <--- Don't forget this import

public interface EmployeeService {
    Page<Employee> getAllEmployees(Pageable pageable);
    Employee getEmployeeById(Long id);

    // THIS SIGNATURE MUST MATCH YOUR IMPLEMENTATION
    void saveEmployee(Employee employee, MultipartFile imageFile, MultipartFile docFile);

    void deleteEmployeeById(Long id);
}