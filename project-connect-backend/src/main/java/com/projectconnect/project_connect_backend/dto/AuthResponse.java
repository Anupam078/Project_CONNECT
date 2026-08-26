package com.projectconnect.project_connect_backend.dto;

import com.projectconnect.project_connect_backend.entity.User;

public class AuthResponse {
    private String token;
    private String username;
    private User.Role role;
    private Long userId;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, String username, User.Role role, Long userId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}