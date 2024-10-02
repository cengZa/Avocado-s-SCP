package com.example.myproject.service;

import java.util.List;



import com.example.myproject.model.User;

public interface UserService {


    User loginService(String username, String password);
    boolean preRegisterUser(User user);
    boolean verifyEmail(String email, String token);
    boolean verifyResetEmail(String email, String token);
    List<User> getAllUsers();
    User addUser(String username, String password, String email) throws Exception;
    boolean checkEmailExists(String email);
    User getUserByUsername(String username);
    User updateUserByUsername(String username, User userDetails) throws Exception;
    void deleteUserByUsername(String username) throws Exception;
    List<User> searchUsersByUsername(String username);
    // 重置用户密码
    boolean resetPassword(String email, String newPassword);
}

