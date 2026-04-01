package com.example.demo.repository;

import com.example.demo.dto.AttendanceSummaryDTO;
import com.example.demo.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEmployeeIdOrderByDateDesc(Long employeeId);

    @Query("SELECT new com.example.demo.dto.AttendanceSummaryDTO(" +
            "e.id, e.firstName, e.lastName, " +
            "CAST(SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS long), " +
            "CAST(SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS long), " +
            "CAST(SUM(CASE WHEN a.status = 'LEAVE' THEN 1 ELSE 0 END) AS long), " +
            "CAST(SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) AS long), " +
            "CAST(SUM(CASE WHEN a.status = 'HALF_DAY' THEN 1 ELSE 0 END) AS long) " +
            ") " +
            "FROM Employee e " +
            "LEFT JOIN Attendance a ON e.id = a.employee.id " +
            "AND MONTH(a.date) = :month AND YEAR(a.date) = :year " +
            "GROUP BY e.id, e.firstName, e.lastName")
    List<AttendanceSummaryDTO> findMonthlySummary(@Param("month") int month, @Param("year") int year);
}