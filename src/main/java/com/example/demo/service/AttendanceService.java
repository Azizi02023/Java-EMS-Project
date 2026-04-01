package com.example.demo.service;

import com.example.demo.dto.AttendanceSummaryDTO;
import com.example.demo.model.Attendance;
import java.util.List;

public interface AttendanceService {
    void saveAttendance(Attendance attendance, Long employeeId);
    void saveBulkAttendance(List<Attendance> attendances); // Added for Bulk functionality
    List<Attendance> getAttendanceHistory(Long employeeId);
    List<AttendanceSummaryDTO> getMonthlySummary(int month, int year); // <--- Add this
}