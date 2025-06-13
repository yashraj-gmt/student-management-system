package com.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String password;
    private String confirmPassword; // (Optional: Normally not stored)
    private String registrationToken;
    private LocalDateTime tokenExpiry;

    @ManyToOne(fetch = FetchType.LAZY)
    private Standard standard;

    private Boolean approved;
    private String temporaryPassword;
    private Boolean passwordUpdated;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    // Add failed attempts and lock time for login failure logic
    private int failedAttempts = 0;
    private LocalDateTime lockTime;
    private boolean accountLocked;

    // Account lock checking
    public boolean isAccountLocked() {
        if (lockTime == null) {
            return false;
        }
        LocalDateTime unlockTime = lockTime.plusHours(24);
        return LocalDateTime.now().isBefore(unlockTime);
    }
}
