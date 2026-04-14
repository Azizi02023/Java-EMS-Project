package com.example.demo.service.impl;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.FileStorageService;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        log.info("Fetching all employees - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return employeeRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });
        return convertToDTO(employee);
    }

    @Override
    @Transactional
    public void saveEmployee(EmployeeDTO dto) {

        Employee employee = (dto.getId() != null)
                ? employeeRepository.findById(dto.getId()).orElse(new Employee())
                : new Employee();

        // Set fields
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());
        employee.setSalary(dto.getSalary());
        employee.setHireDate(dto.getHireDate());

        // CLEAN FILE HANDLING
        employee.setProfileImage(
                fileStorageService.storeFile(dto.getImageFile(), employee.getProfileImage())
        );

        employee.setDocument(
                fileStorageService.storeFile(dto.getDocFile(), employee.getDocument())
        );

        employeeRepository.save(employee);

        log.info("Successfully saved employee: {}", employee.getEmail());
    }

    @Override
    @Transactional
    public void deleteEmployeeById(Long id) {
        log.warn("Soft deleting employee with ID: {}", id);
        employeeRepository.deleteById(id);
    }

    @Override
    public Page<EmployeeDTO> searchEmployees(String keyword, Pageable pageable) {
        log.info("Searching employees with keyword: {}", keyword);
        return employeeRepository.findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void restoreEmployee(Long id) {
        log.info("Restoring soft-deleted employee with ID: {}", id);
        employeeRepository.restoreById(id);
    }

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