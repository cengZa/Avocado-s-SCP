package com.example.myproject.model;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "class_schedule")
@ApiModel("班级课程表实体类")
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "课程表ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    @ApiModelProperty(value = "班级ID", example = "1")
    @JsonIgnore // 加上这个注解
    private Class classEntity; // 关联班级实体

    @Column(name = "start_time", nullable = false)
    @ApiModelProperty(value = "上课开始时间", example = "08:00:00")
    private Date startTime; // 上课开始时间
    @Column(name = "end_time", nullable = false)
    @ApiModelProperty(value = "上课结束时间", example = "10:50:00")
    private Date endTime; // 上课结束时间
    @Column(name = "class_days", nullable = false)
    @ApiModelProperty(value = "上课日", example = "[\"Monday\", \"Wednesday\"]")
    private String classDays; // 上课日，使用 JSON 格式存储

    @Enumerated(EnumType.STRING)
    @Column(name = "week_type", nullable = false)
    @ApiModelProperty(value = "上课周类型", example = "EVERY_WEEK")
    private WeekType weekType; // 上课周类型

    @Column(name = "location", nullable = false)
    @ApiModelProperty(value = "上课地点", example = "Room 101")
    private String location; // 上课地点

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }



    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Class getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(Class classEntity) {
        this.classEntity = classEntity;
    }



    public String getClassDays() {
        return classDays;
    }

    public void setClassDays(String classDays) {
        this.classDays = classDays;
    }

    public WeekType getWeekType() {
        return weekType;
    }

    public void setWeekType(WeekType weekType) {
        this.weekType = weekType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

// 上课周类型的枚举
enum WeekType {
    EVERY_WEEK,
    ODD_WEEK,
    EVEN_WEEK
}
