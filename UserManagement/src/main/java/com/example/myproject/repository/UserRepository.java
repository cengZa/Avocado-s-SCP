package com.example.myproject.repository;



import com.example.myproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface   UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username); //通过用户名uname查找用户，注意要按照JPA的格式使用驼峰命名法

    User findByUsernameAndPassword(String username, String password);//通过用户名uname和密码查找用户
    User findByEmail(String email);  // 根据邮箱查找用户
    List<User> findByUsernameContaining(String name);

}
