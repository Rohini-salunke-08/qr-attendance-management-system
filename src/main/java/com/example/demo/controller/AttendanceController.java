package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Attendance;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.StudentRepository;

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

    @Autowired
    private StudentRepository studentRepository;

    // ✅ TEST
    @GetMapping
    public String home(){
        return "Attendance API is running!";
    }

    // ✅ MARK ATTENDANCE (POST - for forms)
    @PostMapping("/mark")
    public String markAttendance(@RequestBody Attendance attendance){

        LocalDate today = LocalDate.now();

        boolean alreadyMarked =
            attendanceRepository.existsByStudentIdAndDateAndLectureNo(
                attendance.getStudentId(),
                today,
                attendance.getLectureNo()
            );

        if(alreadyMarked){
            return "❌ Already marked for this lecture!";
        }

        attendance.setDate(today);
        attendanceRepository.save(attendance);

        return "✅ Attendance marked successfully";
    }

    // ✅ QR SCAN (GET)
    @GetMapping("/mark/{studentId}/{lectureNo}")
    public String markViaScan(
            @PathVariable Long studentId,
            @PathVariable int lectureNo
    ){
        LocalDate today = LocalDate.now();

        boolean alreadyMarked =
            attendanceRepository.existsByStudentIdAndDateAndLectureNo(
                studentId, today, lectureNo
            );

        if(alreadyMarked){
            return "❌ Already marked!";
        }

        Attendance attendance = new Attendance();
        attendance.setStudentId(studentId);
        attendance.setLectureNo(lectureNo);
        attendance.setDate(today);

        attendanceRepository.save(attendance);

        return "✅ Attendance marked successfully!";
    }

    // ⭐ STATS
    @GetMapping("/stats")
    public Map<String, Long> getOverallStats(){

        LocalDate today = LocalDate.now();
        int lectureNo = 1;

        long present = attendanceRepository.countByDateAndLectureNo(today, lectureNo);
        long totalStudents = studentRepository.count();
        long absent = totalStudents - present;

        Map<String, Long> stats = new HashMap<>();
        stats.put("present", present);
        stats.put("absent", absent);
        stats.put("total", totalStudents);

        return stats;
    }

    // 📊 DATE WISE REPORT
    @GetMapping("/report")
    public List<Attendance> getAttendanceByDate(
            @RequestParam String startDate,
            @RequestParam String endDate){

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return attendanceRepository.findByDateBetween(start, end);
    }

    // 📊 LECTURE WISE REPORT (NEW)
    @GetMapping("/report/lecture")
    public List<Attendance> getLectureWiseReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam int lectureNo){

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return attendanceRepository.findByDateBetweenAndLectureNo(start, end, lectureNo);
    }

    // 📥 EXPORT EXCEL
    @GetMapping("/export")
    public void exportAttendance(HttpServletResponse response) throws Exception {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename=attendance_report.xlsx");

        List<Attendance> listAttendance = attendanceRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Student ID");
        header.createCell(2).setCellValue("Date");
        header.createCell(3).setCellValue("Lecture");

        int rowCount = 1;

        for (Attendance attendance : listAttendance) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(attendance.getId());
            row.createCell(1).setCellValue(attendance.getStudentId());
            row.createCell(2).setCellValue(attendance.getDate().toString());
            row.createCell(3).setCellValue(attendance.getLectureNo());
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}