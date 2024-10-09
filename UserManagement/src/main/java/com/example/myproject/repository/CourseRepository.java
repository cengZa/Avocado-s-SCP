package com.example.myproject.repository;

import com.example.myproject.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findById(Long courseId); // 根据课程ID查找课程


}
