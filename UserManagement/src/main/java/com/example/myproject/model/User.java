package com.example.myproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name="user")
@Entity
@ApiModel("用户实体类") // 用于描述模型
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "用户ID", example = "1") // 用于描述属性
     private Long id;

    @JsonProperty("username") // 将属性名映射为 JSON 字段名
    @ApiModelProperty(value = "用户名", example = "cyl")
    private String username;

    @JsonProperty("password") // 将属性名映射为 JSON 字段名
    @ApiModelProperty(value = "密码", example = "123")
    private String password;

    @JsonProperty("status") // 将属性名映射为 JSON 字段名
    @ApiModelProperty(value = "用户状态", example = "1")
    private int status;

    @ApiModelProperty(value = "电子邮件地址", example = "john_doe@example.com")
    private String email;

    @ApiModelProperty(value = "邮箱验证状态", example = "true")
    private boolean emailVerified;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
