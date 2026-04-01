package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSummaryDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private Long presentCount;
    private Long absentCount;
    private Long leaveCount;
    private Long lateCount;
    private Long halfDayCount;
}