package com.example.myproject.controller;




import com.example.myproject.model.User;
import com.example.myproject.model.VerificationToken;
import com.example.myproject.service.UserService;
import com.example.myproject.service.EmailService;
import com.example.myproject.repository.UserRepository;
import com.example.myproject.repository.VerificationTokenRepository;
import com.example.myproject.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(value = "用户管理接口", tags = {"用户管理"})
public class UserController {
    @Resource
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);



    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "使用用户名和密码进行登录")
    public Result loginController(@RequestParam @ApiParam(value = "用户名", required = true) String username,
                                  @RequestParam @ApiParam(value = "密码", required = true) String password) {
        try {
            // 使用Spring Security的AuthenticationManager来进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            // 认证成功后，将Authentication对象设置到SecurityContextHolder中
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取当前用户的详细信息

            System.out.println("Login successful for user: " + username);

            return Result.success();
        } catch (AuthenticationException e) {
            return Result.error("401", "登录失败：" + e.getMessage());
        }
    }



    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "使用用户信息进行注册")
    public Result registerController(@RequestBody @ApiParam(value = "新用户信息", required = true) User newUser) {
        if (userService.checkEmailExists(newUser.getEmail())) {
            return Result.error("邮箱冲突", "邮箱已被使用，请使用其他邮箱注册或找回密码！");
        }
        boolean success = userService.preRegisterUser(newUser);
        if (success) {
            // 可以考虑返回一个基本的成功信息，或者创建一个没有敏感信息的User对象
            User responseUser = new User();
            responseUser.setEmail(newUser.getEmail());  // 仅返回用户的电子邮箱
            return Result.success(responseUser, "验证邮件已发送，请检查您的邮箱。");
        } else {
            return Result.error("注册失败", "账号已存在或注册失败！");
        }
    }
    public static class VerificationRequest {
        private String email;
        private String code;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @ApiOperation(value = "获取所有用户", notes = "获取所有用户的信息")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users, "查找所有用户成功！");
    }

    @PostMapping("/verify-code")
    @ApiOperation(value = "验证邮箱验证码", notes = "验证用户邮箱的验证码")
    public Result verifyEmailCode(@RequestBody @ApiParam(value = "验证请求信息", required = true) VerificationRequest verificationRequest) {
        String email = verificationRequest.getEmail();
        String code = verificationRequest.getCode();
        boolean isVerified = userService.verifyEmail(email, code);
        if (isVerified) {
            return Result.success(null, "邮箱验证成功！");
        } else {
            return Result.error("验证失败", "验证码错误或已过期！");
        }
    }


    @Autowired
    private  EmailService emailService;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  VerificationTokenRepository verificationTokenRepository;

    public static class EmailRequest {
        private String email;

        // Getter and Setter
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @PostMapping("/get-verification-email")
    @ApiOperation(value = "发送验证邮件", notes = "通过电子邮件发送验证邮件")
    public ResponseEntity<Result> sendVerificationEmail(@RequestBody @ApiParam(value = "发送验证请求", required = true)EmailRequest emailVerificationRequest) {
        String email = emailVerificationRequest.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("未找到与该邮箱地址相关联的用户: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error("1","未找到与该邮箱地址相关联的用户"));
        }
        // Generate a verification token
        String token = RandomStringUtils.randomNumeric(6); // Implement this in your UserService
        Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);
        logger.info("生成的验证令牌: {}, 过期时间: {}", token, expiryDate);
        VerificationToken verificationToken = new VerificationToken(
                token,
                user.getUsername(),
                email,
                user.getPassword(),
                expiryDate
        );

        verificationTokenRepository.save(verificationToken);

        logger.info("验证令牌已保存，用户: {}", user.getUsername());

        emailService.sendVerificationEmail(email, token);

        return ResponseEntity.ok(Result.success(200, "验证邮件已发送！"));
    }
    public static class PasswordResetRequest {
        private String email;
        private String code;
        private String newPassword;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
    @PostMapping("/reset-password")
    @ApiOperation(value = "重置密码", notes = "通过邮箱验证码重置密码")
    public Result resetPassword(@RequestBody @ApiParam(value = "重置密码请求", required = true) PasswordResetRequest passwordResetRequest) {
        String email = passwordResetRequest.getEmail();
        String code = passwordResetRequest.getCode();

        String newPassword = passwordResetRequest.getNewPassword();

        // Verify the email code first
        boolean isVerified = userService.verifyResetEmail(email, code);
        if (!isVerified) {
            return Result.error("验证失败", "验证码错误或已过期！");
        }

        // Reset the password
        boolean isPasswordReset = userService.resetPassword(email, newPassword);
        if (isPasswordReset) {
            return Result.success(200, "密码重置成功！");
        } else {
            return Result.error("重置失败", "重置密码失败，请重试！");
        }
    }




}
