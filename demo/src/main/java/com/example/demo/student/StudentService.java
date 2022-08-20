package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student addNewStudent(Student student) {
        Boolean existEmail = studentRepository.selectExistsEmail(student.getEmail());
        if (existEmail){
            throw new IllegalStateException("email taken");
        }
        Student std = studentRepository.save(student);
        return std;
    }

    public void deleteStudent(String studentId) {
        boolean exists = studentRepository.existsById(studentId);
        if (!exists){
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(String studentId, String name, String email) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()){
            throw new IllegalStateException("student with id " + studentId + " does not exist");
        }
        Student student = studentOptional.get();

        if (name != null && !name.equals("") && !name.equals(student.getName())){
            student.setName(name);
        }

        if (email != null && !email.equals("") && !email.equals(student.getEmail())){
            if (studentRepository.findStudentByEmail(email).isPresent()){
                throw new IllegalStateException("email already taken");
            }
            student.setEmail(email);
        }
    }
}
