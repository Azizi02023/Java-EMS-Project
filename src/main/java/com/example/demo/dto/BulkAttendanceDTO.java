package com.example.demo.dto;

import com.example.demo.model.Attendance;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data // Ensure you have this or manual getters/setters for attendanceList
public class BulkAttendanceDTO {
    private List<Attendance> attendanceList = new ArrayList<>();
}