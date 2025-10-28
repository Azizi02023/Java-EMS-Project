package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 1. Import RedirectAttributes

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Display list of employees with pagination
    @GetMapping
    public String viewEmployeeList(Model model, @PageableDefault(size = 5) Pageable pageable) {
        Page<Employee> employeePage = employeeService.getAllEmployees(pageable);
        model.addAttribute("employeePage", employeePage);
        return "list-employees"; // Thymeleaf template name
    }

    // Show form to add a new employee
    @GetMapping("/showNewEmployeeForm")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showNewEmployeeForm(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "add-employee-form"; // Thymeleaf template name
    }

    // Save employee to database
    @PostMapping("/saveEmployee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) { // 2. Add RedirectAttributes

        if (bindingResult.hasErrors()) {
            // Check if it's an update (id is not null)
            if (employee.getId() != null) {
                // If it's an update, return to the edit form
                return "edit-employee-form";
            } else {
                // Otherwise, it's a new employee, return to the add form
                return "add-employee-form";
            }
        }

        // 3. Determine the success message
        String message;
        if (employee.getId() != null) {
            message = "Employee updated successfully!";
        } else {
            message = "Employee added successfully!";
        }

        employeeService.saveEmployee(employee);

        // 4. Add the message as a flash attribute
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/employees";
    }

    // Show form to update an employee
    @GetMapping("/showFormForUpdate/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "edit-employee-form"; // Thymeleaf template name
    }

    // Delete an employee
    @GetMapping("/deleteEmployee/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteEmployee(@PathVariable(value = "id") Long id,
                                 RedirectAttributes redirectAttributes) { // 5. Add RedirectAttributes

        this.employeeService.deleteEmployeeById(id);

        // 6. Add the delete success message
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");

        return "redirect:/employees";
    }
}