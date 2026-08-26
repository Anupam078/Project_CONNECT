package com.projectconnect.project_connect_backend.service;

import com.projectconnect.project_connect_backend.dto.AuthRequest;
import com.projectconnect.project_connect_backend.dto.AuthResponse;
import com.projectconnect.project_connect_backend.entity.Shop;
import com.projectconnect.project_connect_backend.entity.User;
import com.projectconnect.project_connect_backend.repository.ShopRepository;
import com.projectconnect.project_connect_backend.repository.UserRepository;
import com.projectconnect.project_connect_backend.security.CustomUserDetailsService;
import com.projectconnect.project_connect_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse signup(AuthRequest request) {
        try {
            logger.info("SIGNUP ATTEMPT: User='{}', Email='{}', Role='{}'",
                    request.getUsername(), request.getEmail(), request.getRole());

            if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null || request.getRole() == null) {
                logger.error("SIGNUP FAILED: A required field in the request was null.");
                throw new RuntimeException("Request contained null fields.");
            }

            if (userRepository.existsByUsername(request.getUsername())) {
                logger.error("SIGNUP FAILED: Username '{}' already exists.", request.getUsername());
                throw new RuntimeException("Username already exists");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.error("SIGNUP FAILED: Email '{}' already exists.", request.getEmail());
                throw new RuntimeException("Email already exists");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setRole(request.getRole());

            logger.info("Attempting to save new user: {}", user.getUsername());
            user = userRepository.save(user);
            logger.info("Successfully saved user with ID: {}", user.getId());

            if (user.getRole() == User.Role.SELLER) {
                Shop shop = new Shop();
                shop.setUserId(user.getId());
                shop.setShopName(user.getUsername() + "'s Shop");
                shopRepository.save(shop);
                logger.info("Successfully created shop for seller.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());
            logger.info("Signup successful for user '{}'", user.getUsername());
            return new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());

        } catch (Exception e) {
            logger.error("!!!! MAJOR SIGNUP EXCEPTION !!!!", e);
            throw new RuntimeException("Signup failed due to an unexpected server error", e);
        }
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
    }
}