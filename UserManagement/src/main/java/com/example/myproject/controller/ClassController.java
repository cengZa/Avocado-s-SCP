package com.example.myproject.controller;

import com.example.myproject.service.ClassService;
import com.example.myproject.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/{classId}")
    public Result getClassDetails(@PathVariable long classId) {
        return classService.getClassDetails(classId);
    }
}
