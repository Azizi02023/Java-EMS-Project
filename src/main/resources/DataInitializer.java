package com.example.demo.config;

import com.example.demo.model.Employee;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private EmployeeRepository employeeRepository; // <--- Add this
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. Create Login Roles
        Role adminRole = createRoleIfNotFound("ADMIN");
        Role managerRole = createRoleIfNotFound("MANAGER");
        Role userRole = createRoleIfNotFound("USER");

        // 2. Create Login Users
        createUserIfNotFound("admin", "password123", Set.of(adminRole, managerRole, userRole));
        createUserIfNotFound("manager", "password123", Set.of(managerRole, userRole));
        createUserIfNotFound("user", "password123", Set.of(userRole));

        // 3. Create Dummy Employees (So the list is not empty)
        createEmployeeIfNotFound("John", "Doe", "john.doe@example.com", "IT", "Developer");
        createEmployeeIfNotFound("Jane", "Smith", "jane.smith@example.com", "HR", "Recruiter");
    }

    // --- Helper Methods ---

    private Role createRoleIfNotFound(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role = roleRepository.save(role);
        }
        return role;
    }

    private void createUserIfNotFound(String username, String password, Set<Role> roles) {
        if (userRepository.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("Created Login User: " + username);
        }
    }

    private void createEmployeeIfNotFound(String fName, String lName, String email, String dept, String position) {
        // Simple check to avoid duplicates on restart
        if (employeeRepository.findAll().stream().noneMatch(e -> e.getEmail().equalsIgnoreCase(email))) {
            Employee emp = new Employee();
            emp.setFirstName(fName);
            emp.setLastName(lName);
            emp.setEmail(email);
            emp.setDepartment(dept);
            emp.setPosition(position);
            emp.setSalary(new BigDecimal("50000"));
            emp.setHireDate(LocalDate.now());
            emp.setPhone("1234567890");

            employeeRepository.save(emp);
            System.out.println("Created Employee: " + fName);
        }
    }
}