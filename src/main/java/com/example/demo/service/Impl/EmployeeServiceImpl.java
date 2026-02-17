package com.example.demo.service.impl;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.FileStorageService;
import lombok.extern.slf4j.Slf4j; // Proper SLF4J logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j // Enables logging via 'log' variable
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        log.info("Fetching all active employees with pagination: {}", pageable);
        return employeeRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    public Page<EmployeeDTO> searchEmployees(String keyword, Pageable pageable) {
        log.info("Searching employees with keyword: {}", keyword);
        // Assuming you add a 'search' method to your repository
        return employeeRepository.findByFirstNameContainingOrEmailContaining(keyword, keyword, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        log.info("Fetching employee ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return convertToDTO(employee);
    }

    @Override
    @Transactional
    public void saveEmployee(EmployeeDTO dto, MultipartFile imageFile, MultipartFile docFile) {
        Employee employee;
        if (dto.getId() != null) {
            log.info("Updating existing employee ID: {}", dto.getId());
            employee = employeeRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot update: Employee not found"));
        } else {
            log.info("Creating new employee record");
            employee = new Employee();
        }

        // Map DTO fields to Entity
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());
        employee.setSalary(dto.getSalary());
        employee.setHireDate(dto.getHireDate());
        employee.setPhone(dto.getPhone());

        // File handling
        if (imageFile != null && !imageFile.isEmpty()) {
            employee.setProfileImage(fileStorageService.storeFile(imageFile));
        }
        if (docFile != null && !docFile.isEmpty()) {
            employee.setDocument(fileStorageService.storeFile(docFile));
        }

        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployeeById(Long id) {
        log.warn("Soft deleting employee ID: {}", id);
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void restoreEmployee(Long id) {
        log.info("Restoring soft-deleted employee ID: {}", id);
        employeeRepository.restoreById(id);
    }

    // Helper method to convert Entity to DTO
    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setDepartment(employee.getDepartment());
        dto.setPosition(employee.getPosition());
        dto.setSalary(employee.getSalary());
        dto.setHireDate(employee.getHireDate());
        dto.setProfileImage(employee.getProfileImage());
        dto.setDocument(employee.getDocument());
        return dto;
    }
}