package com.example.myproject.service;

public interface EmailService {
    /**
     * 发送邮件给指定的邮箱。
     *
     * @param to 收件人邮箱地址
     * @param subject 邮件主题
     * @param text 邮件内容
     * @return 发送是否成功
     */
    boolean sendEmail(String to, String subject, String text);

    void sendVerificationEmail(String email, String token);

}