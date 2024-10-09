package com.example.myproject.repository;

import com.example.myproject.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long> {
    List<Class> findByIdIn(List<Long> classIds); // 根据班级ID列表查找班级

    // 根据教师ID查找班级
    List<Class> findByTeacher_Id(Long teacherId); // 使用 teacher_Id 访问 Teacher 实体的 ID
}
