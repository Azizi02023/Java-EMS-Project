/*package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // --- Create Roles if they don't exist ---
        Role adminRole = createRoleIfNotFound("ADMIN");
        Role managerRole = createRoleIfNotFound("MANAGER");
        Role userRole = createRoleIfNotFound("USER");

        // --- Create Users if they don't exist ---

        // 1. Admin User
        createUserIfNotFound("admin", "password123", Set.of(adminRole, managerRole, userRole));

        // 2. Manager User
        createUserIfNotFound("manager", "password123", Set.of(managerRole, userRole));

        // 3. Basic User
        createUserIfNotFound("user", "password123", Set.of(userRole));
    }

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
            user.setPassword(passwordEncoder.encode(password)); // Encode the password!
            user.setRoles(roles);
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("Created user: " + username);
        }
    }
} */