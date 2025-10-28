package com.example.demo.service;



import com.example.demo.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<Employee> getAllEmployees(Pageable pageable);
    Employee getEmployeeById(Long id);
    void saveEmployee(Employee employee);
    void deleteEmployeeById(Long id);
}