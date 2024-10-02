package com.example.myproject.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.Instant;

@Entity
@ApiModel("验证令牌实体类")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "令牌ID")
    private Long id;

    @ApiModelProperty(value = "令牌字符串")
    private String token;

    @ApiModelProperty(value = "令牌过期日期")
    private Instant expiryDate;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "电子邮件地址")
    private String email;

    @ApiModelProperty(value = "加密后的密码")
    private String password;  // 考虑安全性，这应当是加密后的密码

    // Constructors, getters, and setters

    public VerificationToken() {
    }

    public VerificationToken(String token, String username, String email, String password, Instant expiryDate) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.password = password;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
