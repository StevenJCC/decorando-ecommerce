package com.mdtalalwasim.ecommerce.service;

import java.util.List;
import com.mdtalalwasim.ecommerce.entity.User;

public interface UserService {

    User saveUser(User user);

    User getUserByEmail(String email);
    
    User getUserById(Long id);  // ✅ AGREGADO

    List<User> getAllUsersByRole(String role);

    Boolean updateUserStatus(Boolean status, Long id);

    void userFailedAttemptIncrease(User user);

    void userAccountLock(User user);

    boolean isUnlockAccountTimeExpired(User user);

    void userFailedAttempt(int userId);

    void updateUserResetTokenForSendingEmail(String email, String resetToken);

    User getUserByresetTokens(String token);

    User updateUserWhileResetingPassword(User user);

    List<String> getUserEmailsByType(String userType);

    List<User> getAllUsers();

    void deleteUserById(Long id);
}
