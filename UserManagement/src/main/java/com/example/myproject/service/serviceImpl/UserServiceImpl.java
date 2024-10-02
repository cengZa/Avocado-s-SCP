package com.example.myproject.service.serviceImpl;


import com.example.myproject.model.User;
import com.example.myproject.model.VerificationToken;
import com.example.myproject.repository.UserRepository;
import com.example.myproject.repository.VerificationTokenRepository;
import com.example.myproject.service.EmailService;
import com.example.myproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;

/**
 * The type User service.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;  // 注入密码编码器

    //loginService已经不需要，可以从实现和接口中一起移除
    @Override
    public User loginService(String username, String password) {
        log.debug("尝试登录，用户名：{}，密码：{}", username, password);
        User user = userRepository.findByUsernameAndPassword(username, password);
        if(user != null){
            user.setPassword("");
            log.debug("登录成功，用户ID：{}", user.getId());
        } else {
            log.debug("登录失败，用户名或密码错误");
        }
        return user;
    }

    @Override
    public boolean preRegisterUser(User user) {
        log.debug("预注册用户，用户名：{}，邮箱：{}", user.getUsername(), user.getEmail());
        if (userRepository.findByUsername(user.getUsername()) != null || userRepository.findByEmail(user.getEmail()) != null) {
            log.debug("预注册失败，用户名或邮箱已存在");
            return false;  // 用户名或邮箱已存在
        }

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        String token = RandomStringUtils.randomNumeric(6);
        Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);

        VerificationToken verificationToken = new VerificationToken(
                token,
                user.getUsername(),
                user.getEmail(),
                encryptedPassword, // 密码是加密后的
                expiryDate
        );

        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), token);
        log.debug("预注册成功，生成的验证码：{}，过期时间：{}", token, expiryDate);
        return true;
    }

    @Override
    public boolean verifyEmail(String email, String token) {
        log.debug("验证邮箱，邮箱：{}，验证码：{}", email, token);
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndEmail(token, email);
        if (verificationToken != null && !verificationToken.isExpired()) {
            log.debug("验证码有效，用户名：{}", verificationToken.getUsername());
            User user = userRepository.findByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(verificationToken.getUsername()); // 使用 VerificationToken 中的用户名
                user.setPassword(verificationToken.getPassword()); // 使用 VerificationToken 中的密码
                user.setEmailVerified(true);  // 设置邮箱为已验证
                userRepository.save(user);  // 保存新创建的用户
                log.debug("新用户创建成功，用户ID：{}", user.getId());
            } else {
                user.setEmailVerified(true);  // 更新已有用户的验证状态
                userRepository.save(user);
                log.debug("用户邮箱验证成功，用户ID：{}", user.getId());
            }

            verificationTokenRepository.delete(verificationToken);  // 清理已使用的令牌
            log.debug("验证码已删除");
            return true;
        }
        log.debug("验证码无效或已过期");
        return false;
    }
    @Override
    public boolean verifyResetEmail(String email, String token) {
        log.debug("验证邮箱，邮箱：{}，验证码：{}", email, token);
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndEmail(token, email);
        if (verificationToken != null && !verificationToken.isExpired()) {
            log.debug("验证码有效，用户名：{}", verificationToken.getUsername());
            User user = userRepository.findByEmail(email);

                user.setEmailVerified(true);  // 更新已有用户的验证状态
                log.debug("用户邮箱验证成功，用户ID：{}", user.getId());

            verificationTokenRepository.delete(verificationToken);  // 清理已使用的令牌
            log.debug("验证码已删除");
            return true;
        }
        log.debug("验证码无效或已过期");
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("获取所有用户");
        List<User> users = userRepository.findAll();
        log.debug("获取到用户数量：{}", users.size());
        return users;
    }

    public boolean checkEmailExists(String email) {
        log.debug("检查邮箱是否存在，邮箱：{}", email);
        boolean exists = userRepository.findByEmail(email) != null;
        log.debug("邮箱存在状态：{}", exists);
        return exists;
    }

    @Override
    public User addUser(String username, String password, String email) throws Exception {
        log.debug("添加用户，用户名：{}，邮箱：{}", username, email);
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
            log.debug("添加用户失败，用户名、密码和邮箱不能为空");
            throw new Exception("用户名、密码和邮箱不能为空！");
        }

        if (userRepository.findByUsername(username) != null) {
            log.debug("添加用户失败，用户名已存在");
            throw new Exception("用户名已存在！");
        }

        if (userRepository.findByEmail(email) != null) {
            log.debug("添加用户失败，邮箱已存在");
            throw new Exception("邮箱已存在！");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // 对密码进行加密处理
        user.setEmail(email);

        user = userRepository.save(user);
        log.debug("用户添加成功，用户ID：{}", user.getId());
        return user;
    }


    @Override
    public User getUserByUsername(String username) {
        log.debug("根据用户名获取用户，用户名：{}", username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            log.debug("用户找到，用户ID：{}", user.getId());
        } else {
            log.debug("用户未找到");
        }
        return user;
    }

    @Override
    public User updateUserByUsername(String username, User userDetails) throws Exception {
        log.debug("更新用户信息，用户名：{}", username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            user.setPassword(passwordEncoder.encode(userDetails.getPassword())); // 加密密码
            user.setEmail(userDetails.getEmail());
            user = userRepository.save(user);
            log.debug("用户信息更新成功，用户ID：{}", user.getId());
            return user;
        } else {
            log.debug("更新用户信息失败，用户不存在");
            throw new Exception("用户不存在！");
        }
    }

    @Override
    public void deleteUserByUsername(String username) throws Exception {
        log.debug("删除用户，用户名：{}", username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
            log.debug("用户删除成功，用户ID：{}", user.getId());
        } else {
            log.debug("删除用户失败，用户不存在");
            throw new Exception("用户不存在！");
        }
    }

    @Override
    public List<User> searchUsersByUsername(String username) {
        log.debug("根据用户名搜索用户，用户名包含：{}", username);
        List<User> users = userRepository.findByUsernameContaining(username);
        log.debug("搜索到用户数量：{}", users.size());
        return users;
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            // 使用BCrypt加密密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user); // 保存更新后的用户信息
            return true;
        }
        return false;
    }

}
