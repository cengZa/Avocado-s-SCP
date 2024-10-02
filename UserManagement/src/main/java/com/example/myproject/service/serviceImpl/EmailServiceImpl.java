package com.example.myproject.service.serviceImpl;



import com.example.myproject.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")  // 从配置文件中注入发件人邮箱地址
    private String fromEmail;

    @Override
    public boolean sendEmail(String to, String subject, String text) {
        log.debug("开始发送验证邮件到：{}，主题：{}", to, subject);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);  // 设置发件人邮箱地址
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);  // true indicates the text includes HTML

            mailSender.send(message);
            log.info("邮件成功发送到：{}", to);
            return true;
        } catch (MessagingException e) {
            log.error("发送邮件失败，收件人：{}，错误信息：{}", to, e.getMessage(), e);
            return false;
        }
    }
    @Override
    public void sendVerificationEmail(String email, String token) {
        String message = "<h1>Email Verification</h1>" +
                "<p>Your verification code is: " + token + "</p>" +
                "<p>Please enter this code on the verification page to complete your registration.</p>";
        sendEmail(email, "Verify Your Email", message);
    }

}