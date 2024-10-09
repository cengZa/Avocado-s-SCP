package com.example.myproject.repository;

import com.example.myproject.dto.ClassCourseInfoDTO;
import com.example.myproject.model.ClassSchedule;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByClassEntity_Id(Long classId); // 查询方法
//    @Query(value = "SELECT cs.id, cs.class_id, cs.start_time, cs.end_time, cs.class_days, cs.week_type, cs.location, c.course_name " +
//            "FROM class_schedule cs " +
//            "JOIN class cl ON cs.class_id = cl.id " +
//            "JOIN course c ON cl.course_id = c.id " +
//            "WHERE JSON_CONTAINS(cs.class_days, JSON_QUOTE(DAYNAME(CURDATE()))) " +
//            "AND ((cs.week_type = 'EVERY_WEEK') OR " +
//            "(cs.week_type = 'ODD_WEEK' AND WEEK(CURDATE()) % 2 = 1) OR " +
//            "(cs.week_type = 'EVEN_WEEK' AND WEEK(CURDATE()) % 2 = 0))",
//            nativeQuery = true)
@Query("SELECT cs FROM ClassSchedule cs " +
        "WHERE cs.classEntity.id = :classId")
List<ClassSchedule> findByClassId(@Param("classId") long classId);


}
