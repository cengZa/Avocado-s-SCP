package com.example.myproject.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Date;
import java.sql.Time;


@ApiModel("课程与班级信息 DTO")
public class ClassCourseInfoDTO {

    @ApiModelProperty(value = "课程ID", example = "101")
    private Long courseId;

    @ApiModelProperty(value = "课程号", example = "CS101")
    private String courseCode;

    @ApiModelProperty(value = "课程名称", example = "计算机科学导论")
    private String courseName;

    @ApiModelProperty(value = "课程描述", example = "本课程介绍计算机科学的基础知识")
    private String courseDescription;



    @ApiModelProperty(value = "班级Id", example = "1")
    private Long classId;

    @ApiModelProperty(value = "班级序列", example = "1")
    private int classSequence;

    public ClassCourseInfoDTO(Long courseId, String courseCode, String courseName, String courseDescription,Long classId, int classSequence) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.classId=classId;
        this.classSequence = classSequence;

    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }


    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public int getClassSequence() {
        return classSequence;
    }

    public void setClassSequence(int classSequence) {
        this.classSequence = classSequence;
    }
}
