package com.example.myproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name="course")
@Entity
@ApiModel("课程实体类") // 用于描述模型
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "课程ID", example = "101") // 用于描述属性
    private Long id;

    @JsonProperty("course_code") // 将属性名映射为 JSON 字段名
    @ApiModelProperty(value = "课程号", example = "CS101")
    private String courseCode;

    @JsonProperty("course_name") // 将属性名映射为 JSON 字段名
    @ApiModelProperty(value = "课程名称", example = "计算机科学导论")
    private String courseName;



    @JsonProperty("description") // 将属性名映射为 JSON 字段名
    @ApiModelProperty(value = "课程描述", example = "本课程介绍计算机科学的基础知识")
    private String courseDescription;



    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


}
