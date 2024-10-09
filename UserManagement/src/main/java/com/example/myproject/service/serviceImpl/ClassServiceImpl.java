package com.example.myproject.service.serviceImpl;

import com.example.myproject.model.ClassSchedule;
import com.example.myproject.repository.ClassScheduleRepository;
import com.example.myproject.service.ClassService;
import com.example.myproject.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Override
    @Transactional(readOnly = true)
    public Result getClassDetails(long classId) {
        // 查询班级信息
        List<ClassSchedule> classSchedules = classScheduleRepository. findByClassId(classId);

        if (classSchedules.isEmpty()) {
            return Result.error("500","班级信息不存在");
        }

        // 返回班级信息
        return Result.success(classSchedules);
    }
}
