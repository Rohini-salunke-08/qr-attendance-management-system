package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

    boolean existsByStudentIdAndDate(Long studentId, LocalDate date);

    long countByStudentId(Long studentId);

    long countByDate(LocalDate date);

    boolean existsByStudentIdAndDateAndLectureNo(
            Long studentId, LocalDate date, int lectureNo);

    long countByDateAndLectureNo(LocalDate date, int lectureNo);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // ✅ LECTURE WISE REPORT
    List<Attendance> findByDateBetweenAndLectureNo(
            LocalDate startDate,
            LocalDate endDate,
            int lectureNo
    );
}