package com.example.demo.controller;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        // Page<EmployeeDTO> is returned from the updated service
        Page<EmployeeDTO> employeePage = employeeService.getAllEmployees(pageable);
        model.addAttribute("employeePage", employeePage);
        return "list-employees";
    }

    @GetMapping("/showNewEmployeeForm")
    // REMOVED @PreAuthorize because browser links cannot send JWT headers
    public String showNewEmployeeForm(Model model) {
        model.addAttribute("employee", new EmployeeDTO());
        return "add-employee-form";
    }

    @PostMapping("/saveEmployee")
    // Security will be handled by the JavaScript 'fetch' call sending the token
    public String saveEmployee(@Valid @ModelAttribute("employee") EmployeeDTO employee,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return employee.getId() != null ? "edit-employee-form" : "add-employee-form";
        }

        try {
            employeeService.saveEmployee(employee, employee.getImageFile(), employee.getDocFile());
            redirectAttributes.addFlashAttribute("successMessage", "Employee saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving employee: " + e.getMessage());
        }

        return "redirect:/employees";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "edit-employee-form";
    }

    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable(value = "id") Long id, RedirectAttributes redirectAttributes) {
        try {
            this.employeeService.deleteEmployeeById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/employees";
    }
}