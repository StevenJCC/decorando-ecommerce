package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;  // <-- AQUI INYECTAMOS EL EMAIL SERVICE

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isBlank()) user.setRole("ROLE_USER");
        if (user.getIsEnable() == null) user.setIsEnable(true);
        if (user.getAccountStatusNonLocked() == null) user.setAccountStatusNonLocked(true);
        if (user.getAccountfailedAttemptCount() == null) user.setAccountfailedAttemptCount(0);
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // ENVÍO DE CORREO DESPUÉS DE GUARDAR
        if (!ObjectUtils.isEmpty(savedUser)) {
            emailService.enviarCorreoRegistro(savedUser.getEmail(), savedUser.getFullName());
        }

        return savedUser;
    }

    // TODO: El resto lo dejamos igual, no tocamos nada:
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateUserStatus(Boolean status, Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setIsEnable(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void userFailedAttemptIncrease(User user) {
        int failedAttempt = (user.getAccountfailedAttemptCount() != null) ? user.getAccountfailedAttemptCount() + 1 : 1;
        user.setAccountfailedAttemptCount(failedAttempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(User user) {
        user.setAccountStatusNonLocked(false);
        user.setAccountLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean isUnlockAccountTimeExpired(User user) {
        Date lockTime = user.getAccountLockTime();
        if (lockTime != null) {
            long lockTimeInMillis = lockTime.getTime();
            long currentTimeInMillis = System.currentTimeMillis();
            return (currentTimeInMillis - lockTimeInMillis) > (24 * 60 * 60 * 1000);
        }
        return false;
    }

    @Override
    public void userFailedAttempt(int userId) {
        // No implementado
    }

    @Override
    public void updateUserResetTokenForSendingEmail(String email, String resetToken) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetTokens(resetToken);
            userRepository.save(user);
        }
    }

    @Override
    public User getUserByresetTokens(String token) {
        return userRepository.findByResetTokens(token);
    }

    @Override
    public User updateUserWhileResetingPassword(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<String> getUserEmailsByType(String userType) {
        List<User> users;
        if (userType == null || userType.equals("all")) {
            users = userRepository.findAll();
        } else if (userType.equals("customer")) {
            users = userRepository.findByRole("ROLE_USER");
        } else if (userType.equals("admin")) {
            users = userRepository.findByRole("ROLE_ADMIN");
        } else {
            users = userRepository.findAll();
        }
        return users.stream()
                .filter(user -> user.getEmail() != null && !user.getEmail().isEmpty())
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
