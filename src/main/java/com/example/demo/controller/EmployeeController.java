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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String viewEmployeeList(Model model, @PageableDefault(size = 5) Pageable pageable) {
        Page<Employee> employeePage = employeeService.getAllEmployees(pageable);
        model.addAttribute("employeePage", employeePage);
        return "list-employees";
    }

    @GetMapping("/showNewEmployeeForm")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showNewEmployeeForm(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "add-employee-form";
    }

    @PostMapping("/saveEmployee")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult bindingResult,
                               // Removed @RequestParam for files as they are now in the Employee object
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (bindingResult.hasErrors()) {
            if (employee.getId() != null) {
                try {
                    Employee existingEmployee = employeeService.getEmployeeById(employee.getId());
                    // Keep the old file names in the object being returned to the view
                    if (employee.getProfileImage() == null || employee.getProfileImage().isEmpty()) {
                        employee.setProfileImage(existingEmployee.getProfileImage());
                    }
                    if (employee.getDocument() == null || employee.getDocument().isEmpty()) {
                        employee.setDocument(existingEmployee.getDocument());
                    }
                } catch (Exception e) {
                    // Ignore if not found during validation phase
                }
                return "edit-employee-form";
            } else {
                return "add-employee-form";
            }
        }

        try {
            // Access files directly from the employee object
            employeeService.saveEmployee(employee, employee.getImageFile(), employee.getDocFile());

            String message = (employee.getId() != null) ? "Employee updated successfully!" : "Employee added successfully!";
            redirectAttributes.addFlashAttribute("successMessage", message);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving employee: " + e.getMessage());
        }

        return "redirect:/employees";
    }

    @GetMapping("/showFormForUpdate/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "edit-employee-form";
    }

    @GetMapping("/deleteEmployee/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteEmployee(@PathVariable(value = "id") Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            this.employeeService.deleteEmployeeById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting employee: " + e.getMessage());
        }
        return "redirect:/employees";
    }
}