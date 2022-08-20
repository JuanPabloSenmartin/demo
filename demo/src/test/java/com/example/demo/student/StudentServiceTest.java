package com.example.demo.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }



    @Test
    void canGetStudents() {
        //when
        underTest.getStudents();
        //then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddNewStudent() {
        //given
        Student student = new Student("Jamila", "Jamila@gmail.com", LocalDate.of(2000, Month.JANUARY, 5));
        //when
        underTest.addNewStudent(student);
        //then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailTaken() {
        //given
        Student student = new Student("Jamila", "Jamila@gmail.com", LocalDate.of(2000, Month.JANUARY, 5));

        given(studentRepository.selectExistsEmail(student.getEmail())).willReturn(true);
        //when
        //then

        assertThatThrownBy(() -> underTest.addNewStudent(student)).isInstanceOf(IllegalStateException.class).hasMessageContaining("email taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        //given
        Student student = new Student("Jamila", "Jamila@gmail.com", LocalDate.of(2000, Month.JANUARY, 5));
        underTest.addNewStudent(student);
        //when
        underTest.deleteStudent(student.getId());
        //then
        ArgumentCaptor<String> studentIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(studentRepository).deleteById(studentIdArgumentCaptor.capture());
        String capturedStudentId = studentIdArgumentCaptor.getValue();
        assertThat(capturedStudentId).isEqualTo(student.getId());
    }
}