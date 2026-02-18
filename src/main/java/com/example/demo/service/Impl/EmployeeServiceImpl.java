package com.example.demo.service.impl;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.FileStorageService;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return convertToDTO(employee);
    }

    @Override
    @Transactional
    public void saveEmployee(EmployeeDTO dto, MultipartFile imageFile, MultipartFile docFile) {
        Employee employee = (dto.getId() != null)
                ? employeeRepository.findById(dto.getId()).orElse(new Employee())
                : new Employee();

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());
        employee.setSalary(dto.getSalary());
        employee.setHireDate(dto.getHireDate());

        if (imageFile != null && !imageFile.isEmpty()) {
            employee.setProfileImage(fileStorageService.storeFile(imageFile));
        }
        if (docFile != null && !docFile.isEmpty()) {
            employee.setDocument(fileStorageService.storeFile(docFile));
        }

        employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Page<EmployeeDTO> searchEmployees(String keyword, Pageable pageable) {
        return null; // Implement as needed
    }

    @Override
    public void restoreEmployee(Long id) {
        // Implement as needed
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