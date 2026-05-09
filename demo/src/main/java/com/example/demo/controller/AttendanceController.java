package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Attendance;
import com.example.demo.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // ✅ MARK ATTENDANCE
    @PostMapping
    public String markAttendance(@RequestBody Attendance attendance){

        LocalDate today = LocalDate.now();

        boolean alreadyMarked =
        attendanceRepository.existsByStudentIdAndDate(attendance.getStudentId(), today);

        if(alreadyMarked){
            return "Attendance already marked today";
        }

        attendance.setDate(today);

        attendanceRepository.save(attendance);

        return "Attendance marked successfully";
    }

    // ✅ ATTENDANCE PERCENTAGE
    @GetMapping("/percentage/{studentId}")
    public double getAttendancePercentage(@PathVariable Long studentId){

        long presentDays = attendanceRepository.countByStudentId(studentId);

        long totalClasses = 30;

        double percentage = ((double) presentDays / totalClasses) * 100;

        return Math.round(percentage * 100.0) / 100.0;
    }

    // ✅ STUDENT-WISE STATS
    @GetMapping("/stats/{studentId}")
    public Map<String, Long> getAttendanceStats(@PathVariable Long studentId){

        long presentDays = attendanceRepository.countByStudentId(studentId);

        long totalClasses = 30;

        long absentDays = totalClasses - presentDays;

        Map<String, Long> stats = new HashMap<>();

        stats.put("present", presentDays);
        stats.put("absent", absentDays);

        return stats;
    }

    // ⭐⭐ NEW API (IMPORTANT FOR DASHBOARD)
    @GetMapping("/stats")
    public Map<String, Long> getOverallStats(){

        LocalDate today = LocalDate.now();

        long present = attendanceRepository.countByDate(today); // ✅ only today

        long totalStudents = 14; // or fetch dynamically later

        long absent = totalStudents - present;

        Map<String, Long> stats = new HashMap<>();

        stats.put("present", present);
        stats.put("absent", absent);

        return stats;
    }

    // ✅ EXPORT EXCEL
    @GetMapping("/export")
    public void exportAttendance(HttpServletResponse response) throws Exception {

        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=attendance_report.xlsx";

        response.setHeader(headerKey, headerValue);

        List<Attendance> listAttendance = attendanceRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Student ID");
        header.createCell(2).setCellValue("Date");

        int rowCount = 1;

        for(Attendance attendance : listAttendance){

            Row row = sheet.createRow(rowCount++);

            row.createCell(0).setCellValue(attendance.getId());
            row.createCell(1).setCellValue(attendance.getStudentId());
            row.createCell(2).setCellValue(attendance.getDate().toString());
        }

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        workbook.close();
        outputStream.close();
    }

    // ✅ DATE-WISE REPORT
    @GetMapping("/report")
    public List<Attendance> getAttendanceByDate(
        @RequestParam String startDate,
        @RequestParam String endDate){

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return attendanceRepository.findByDateBetween(start, end);
    }
}