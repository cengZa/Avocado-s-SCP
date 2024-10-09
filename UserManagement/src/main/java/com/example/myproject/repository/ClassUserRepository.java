package com.example.myproject.repository;

import com.example.myproject.dto.ClassCourseInfoDTO;
import com.example.myproject.model.ClassUser;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassUserRepository extends JpaRepository<ClassUser, Long> {
    List<ClassUser> findByStudentId(Long studentId); // 根据学生ID查找班级用户
//    @Query("SELECT cu FROM ClassUser cu " +
//            "JOIN FETCH cu.classEntity c " +
//            "JOIN FETCH c.course " +
//            "WHERE cu.student.id = :studentId")
//    List<ClassUser> findByStudentIdWithFetch(@Param("studentId") Long studentId);

@Query("SELECT new com.example.myproject.dto.ClassCourseInfoDTO(" +
        "c.course.id,  " + // 课程ID
        "c.course.courseCode, " + // 课程号
        "c.course.courseName, " + // 课程名称
        "c.course.courseDescription, " + // 课程描述
        "cu.classEntity.id, " + // 班级id
        "cu.classEntity.classSequence) " + // 班级序列
        "FROM ClassUser cu " +
        "JOIN cu.classEntity c " +
        "JOIN c.course " +
        "WHERE cu.student.id = :studentId")

List<ClassCourseInfoDTO> findByStudentIdWithFetch(@Param("studentId") Long studentId);
    @Query("SELECT new com.example.myproject.dto.ClassCourseInfoDTO(" +
            "c.course.id, " + // 课程ID
            "c.course.courseCode, " + // 课程号
            "c.course.courseName, " + // 课程名称
            "c.course.courseDescription, " + // 课程描述
            "c.id, " + // 课程描述
            "c.classSequence)" + // 班级序列
            "FROM Class c " +
            "WHERE c.teacher.id = :teacherId")

   List<ClassCourseInfoDTO> findByTeacherIdWithFetch(@Param("teacherId") Long teacherId);


}
