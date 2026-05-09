package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private LocalDate date;

    public Attendance(){}

    public Attendance(Long studentId, LocalDate date){
        this.studentId = studentId;
        this.date = date;
    }

    public Long getId(){
        return id;
    }

    public Long getStudentId(){
        return studentId;
    }

    public void setStudentId(Long studentId){
        this.studentId = studentId;
    }

    public LocalDate getDate(){
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }
}