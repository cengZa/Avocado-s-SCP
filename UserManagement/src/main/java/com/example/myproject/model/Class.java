package com.example.myproject.model;


import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "class")
@ApiModel("班级实体类")
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "班级ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ApiModelProperty(value = "课程ID", example = "101")
    private Course course; // 关联课程实体

    @ApiModelProperty(value = "课序号", example = "2")
    @Column(name = "class_sequence", nullable = false)
    private int classSequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @ApiModelProperty(value = "教师ID", example = "1001")
    private User teacher; // 关联教师实体

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getClassSequence() {
        return classSequence;
    }

    public void setClassSequence(int classSequence) {
        this.classSequence = classSequence;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }
}
