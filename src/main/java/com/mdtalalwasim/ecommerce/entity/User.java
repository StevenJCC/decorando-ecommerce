package com.mdtalalwasim.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    private String profileImage;

    private Boolean isEnable;

    private Boolean accountStatusNonLocked;

    private Integer accountfailedAttemptCount;

    private String resetTokens;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Date accountLockTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cart> carts;

    // Constructors
    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String password, String role,
                String profileImage, Boolean isEnable, Boolean accountStatusNonLocked,
                Integer accountfailedAttemptCount, String resetTokens, LocalDateTime createdAt,
                LocalDateTime updatedAt, Date accountLockTime, List<Cart> carts) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
        this.isEnable = isEnable;
        this.accountStatusNonLocked = accountStatusNonLocked;
        this.accountfailedAttemptCount = accountfailedAttemptCount;
        this.resetTokens = resetTokens;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountLockTime = accountLockTime;
        this.carts = carts;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getProfileImage() { return profileImage; }

    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Boolean getIsEnable() { return isEnable; }

    public void setIsEnable(Boolean isEnable) { this.isEnable = isEnable; }

    public Boolean getAccountStatusNonLocked() { return accountStatusNonLocked; }

    public void setAccountStatusNonLocked(Boolean accountStatusNonLocked) { this.accountStatusNonLocked = accountStatusNonLocked; }

    public Integer getAccountfailedAttemptCount() { return accountfailedAttemptCount; }

    public void setAccountfailedAttemptCount(Integer accountfailedAttemptCount) { this.accountfailedAttemptCount = accountfailedAttemptCount; }

    public String getResetTokens() { return resetTokens; }

    public void setResetTokens(String resetTokens) { this.resetTokens = resetTokens; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Date getAccountLockTime() { return accountLockTime; }

    public void setAccountLockTime(Date accountLockTime) { this.accountLockTime = accountLockTime; }

    public List<Cart> getCarts() { return carts; }

    public void setCarts(List<Cart> carts) { this.carts = carts; }

    // Método útil para vistas
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}
