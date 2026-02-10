package com.example.demo.service.impl;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new RuntimeException("Employee not found for id :: " + id);
        }
    }

    @Override
    @Transactional
    public void saveEmployee(Employee employee, MultipartFile imageFile, MultipartFile docFile) {
        // Fetch existing employee if updating to preserve data that might be missing from form
        Employee existingEmployee = null;
        if(employee.getId() != null) {
            existingEmployee = employeeRepository.findById(employee.getId()).orElse(null);
        }

        // 1. Handle Profile Image Upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = fileStorageService.storeFile(imageFile);
            employee.setProfileImage(imageFileName);
        } else {
            // Keep old image if editing and no new file
            if(existingEmployee != null) {
                employee.setProfileImage(existingEmployee.getProfileImage());
            }
        }

        // 2. Handle Document Upload
        if (docFile != null && !docFile.isEmpty()) {
            String docFileName = fileStorageService.storeFile(docFile);
            employee.setDocument(docFileName);
        } else {
            // Keep old document if editing and no new file
            if(existingEmployee != null) {
                employee.setDocument(existingEmployee.getDocument());
            }
        }

        // 3. Save to Database
        this.employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployeeById(Long id) {
        this.employeeRepository.deleteById(id);
    }
}