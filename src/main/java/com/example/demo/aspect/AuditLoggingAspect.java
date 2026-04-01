package com.example.demo.aspect;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.AuditLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
public class AuditLoggingAspect {

    private static final Logger logger = LogManager.getLogger(AuditLoggingAspect.class);

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Updated logic to match EmployeeServiceImpl.saveEmployee(EmployeeDTO, MultipartFile, MultipartFile)
     */
    @Around("execution(* com.example.demo.service.EmployeeService.saveEmployee(..))")
    public Object logSaveEmployee(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        // Check if the first argument is EmployeeDTO (which is what your Service uses)
        if (args.length == 0 || !(args[0] instanceof EmployeeDTO)) {
            return joinPoint.proceed();
        }

        EmployeeDTO dto = (EmployeeDTO) args[0];
        String currentUsername = getCurrentUsername();
        String action;
        String details = "";

        if (dto.getId() != null) {
            // --- UPDATE LOGIC ---
            action = "EMPLOYEE_UPDATED";
            Optional<Employee> oldEmployeeOptional = employeeRepository.findById(dto.getId());

            if (oldEmployeeOptional.isPresent()) {
                // Compare the DTO fields against the existing Database Entity
                details = calculateDiff(oldEmployeeOptional.get(), dto);
            } else {
                details = "Updated Employee ID: " + dto.getId();
            }
        } else {
            // --- CREATE LOGIC ---
            action = "EMPLOYEE_CREATED";
            details = "New Employee: " + dto.getFirstName() + " " + dto.getLastName();
        }

        Object result = joinPoint.proceed();

        // Only save log if there's a real change or a creation
        if (!details.isEmpty() && !details.equals("No detected changes.")) {
            logger.info("Audit: Action={}, User={}, Details={}", action, currentUsername, details);
            auditLogService.saveLog(action, currentUsername, details);
        }

        return result;
    }

    @AfterReturning("execution(* com.example.demo.service.EmployeeService.deleteEmployeeById(..))")
    public void logDeleteEmployee(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Long) {
            Long id = (Long) args[0];
            String currentUsername = getCurrentUsername();
            String details = "Soft Deleted Employee ID: " + id;

            logger.info("Audit: Action=EMPLOYEE_DELETED, User={}, Details={}", currentUsername, details);
            auditLogService.saveLog("EMPLOYEE_DELETED", currentUsername, details);
        }
    }

    /**
     * Helper to compare existing Employee entity with incoming EmployeeDTO
     */
    private String calculateDiff(Employee oldEmp, EmployeeDTO newDto) {
        List<String> changes = new ArrayList<>();

        if (!Objects.equals(oldEmp.getFirstName(), newDto.getFirstName())) {
            changes.add("First Name: " + oldEmp.getFirstName() + " -> " + newDto.getFirstName());
        }
        if (!Objects.equals(oldEmp.getLastName(), newDto.getLastName())) {
            changes.add("Last Name: " + oldEmp.getLastName() + " -> " + newDto.getLastName());
        }
        if (!Objects.equals(oldEmp.getEmail(), newDto.getEmail())) {
            changes.add("Email: " + oldEmp.getEmail() + " -> " + newDto.getEmail());
        }
        if (oldEmp.getSalary() != null && newDto.getSalary() != null &&
                oldEmp.getSalary().compareTo(newDto.getSalary()) != 0) {
            changes.add("Salary: " + oldEmp.getSalary() + " -> " + newDto.getSalary());
        }
        if (!Objects.equals(oldEmp.getDepartment(), newDto.getDepartment())) {
            changes.add("Dept: " + oldEmp.getDepartment() + " -> " + newDto.getDepartment());
        }

        if (changes.isEmpty()) {
            return "No detected changes.";
        }
        return "ID " + oldEmp.getId() + " changes: " + String.join(", ", changes);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}