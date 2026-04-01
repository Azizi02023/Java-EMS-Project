package com.example.demo.controller;

import com.example.demo.dto.BulkAttendanceDTO;
import com.example.demo.dto.EmployeeDTO;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceStatus;
import com.example.demo.model.Employee;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/attendance")
@Slf4j
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * Shows the monthly attendance report summary.
     */
    @GetMapping("/summary")
    public String showMonthlySummary(@RequestParam(required = false) Integer month,
                                     @RequestParam(required = false) Integer year,
                                     Model model) {
        try {
            int targetMonth = (month == null) ? LocalDate.now().getMonthValue() : month;
            int targetYear = (year == null) ? LocalDate.now().getYear() : year;

            List<com.example.demo.dto.AttendanceSummaryDTO> summary = attendanceService.getMonthlySummary(targetMonth, targetYear);

            model.addAttribute("summaryList", summary);
            model.addAttribute("selectedMonth", targetMonth);
            model.addAttribute("selectedYear", targetYear);
            return "attendance/monthly-summary";
        } catch (Exception e) {
            log.error("Error loading monthly summary: ", e);
            return "error/general";
        }
    }

    /**
     * Shows form to mark attendance for all employees.
     * Auto-fills the current date for every record.
     */
    @GetMapping("/mark-bulk")
    public String showBulkAttendanceForm(Model model) {
        try {
            // Fetch all employees using a PageRequest to avoid UnsupportedOperationException
            List<EmployeeDTO> employees = employeeService.getAllEmployees(PageRequest.of(0, Integer.MAX_VALUE)).getContent();

            BulkAttendanceDTO bulkDTO = new BulkAttendanceDTO();
            List<Attendance> attendanceList = new ArrayList<>();

            for (EmployeeDTO emp : employees) {
                Attendance attendance = new Attendance();
                Employee employee = new Employee();
                employee.setId(emp.getId());
                employee.setFirstName(emp.getFirstName());
                employee.setLastName(emp.getLastName());

                attendance.setEmployee(employee);

                // AUTOMATICALLY FILL CURRENT DATE FOR BULK FORM
                attendance.setDate(LocalDate.now());

                attendance.setStatus(AttendanceStatus.PRESENT);
                attendanceList.add(attendance);
            }

            bulkDTO.setAttendanceList(attendanceList);
            model.addAttribute("bulkDTO", bulkDTO);
            model.addAttribute("statuses", AttendanceStatus.values());
            return "attendance/mark-bulk";
        } catch (Exception e) {
            log.error("Error loading bulk attendance form: ", e);
            return "error/general";
        }
    }

    /**
     * Displays attendance history for a specific employee.
     */
    @GetMapping("/history/{employeeId}")
    public String getEmployeeHistory(@PathVariable Long employeeId, Model model) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeById(employeeId);
            List<Attendance> history = attendanceService.getAttendanceHistory(employeeId);

            model.addAttribute("employee", employee);
            model.addAttribute("attendanceList", history);
            return "attendance/employee-history";
        } catch (Exception e) {
            log.error("Error loading employee history: ", e);
            return "error/general";
        }
    }

    /**
     * Saves the bulk attendance records.
     */
    @PostMapping("/save-bulk")
    public String saveBulkAttendance(@ModelAttribute("bulkDTO") BulkAttendanceDTO bulkDTO,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (bulkDTO.getAttendanceList() == null || bulkDTO.getAttendanceList().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No attendance data received.");
                return "redirect:/attendance/mark-bulk";
            }

            List<Attendance> recordsToSave = bulkDTO.getAttendanceList().stream()
                    .filter(a -> a.getEmployee() != null && a.getEmployee().getId() != null)
                    .toList();

            attendanceService.saveBulkAttendance(recordsToSave);
            redirectAttributes.addFlashAttribute("successMessage", "Bulk attendance saved successfully!");
            return "redirect:/attendance/summary";

        } catch (Exception e) {
            log.error("Bulk Save Error: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Operation Failed: " + e.getMessage());
            return "redirect:/attendance/mark-bulk";
        }
    }
}