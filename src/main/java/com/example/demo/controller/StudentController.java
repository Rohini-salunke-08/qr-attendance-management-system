package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    // ADD STUDENT
    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // GET ALL STUDENTS
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // DELETE STUDENT
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {

        studentRepository.deleteById(id);

        return "Student Deleted Successfully";
    }

    // UPDATE STUDENT
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {

        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));

        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setCourse(studentDetails.getCourse());

        return studentRepository.save(student);
    }
}