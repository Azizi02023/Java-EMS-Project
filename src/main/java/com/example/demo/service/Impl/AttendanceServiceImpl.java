package com.example.demo.service.impl;

import com.example.demo.dto.AttendanceSummaryDTO;
import com.example.demo.model.Attendance;
import com.example.demo.model.Employee;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void saveAttendance(Attendance attendance, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        attendance.setEmployee(employee);
        attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void saveBulkAttendance(List<Attendance> attendances) {
        log.info("Saving bulk attendance records. Count: {}", attendances.size());
        for (Attendance attendance : attendances) {
            if (attendance.getEmployee() != null && attendance.getEmployee().getId() != null) {
                // Ensure we are working with a managed Employee entity from the database
                Employee managedEmployee = employeeRepository.findById(attendance.getEmployee().getId())
                        .orElseThrow(() -> new RuntimeException("Employee ID " + attendance.getEmployee().getId() + " not found"));
                attendance.setEmployee(managedEmployee);
            }

            // Critical Fix: Ensure the status is not null before saving to avoid constraint violations
            if (attendance.getStatus() == null) {
                throw new RuntimeException("Attendance status is missing for employee: " +
                        (attendance.getEmployee() != null ? attendance.getEmployee().getId() : "Unknown"));
            }
        }
        try {
            attendanceRepository.saveAll(attendances);
        } catch (Exception e) {
            log.error("Failed to save bulk attendance: {}", e.getMessage());
            throw new RuntimeException("Could not save attendance records. Check for duplicate dates.");
        }
    }

    @Override
    public List<Attendance> getAttendanceHistory(Long employeeId) {
        log.info("Fetching attendance history for employee ID: {}", employeeId);
        return attendanceRepository.findByEmployeeIdOrderByDateDesc(employeeId);
    }

    @Override
    public List<AttendanceSummaryDTO> getMonthlySummary(int month, int year) {
        log.info("Generating attendance summary for Month: {}, Year: {}", month, year);
        // Ensure the repository query uses LEFT JOIN to include employees with no records
        return attendanceRepository.findMonthlySummary(month, year);
    }
}