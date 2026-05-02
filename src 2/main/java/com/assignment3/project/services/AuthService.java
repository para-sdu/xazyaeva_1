package com.assignment3.project.services;

import com.assignment3.project.dto.requests.LoginRequest;
import com.assignment3.project.dto.requests.RegisterRequest;
import com.assignment3.project.dto.responses.LoginResponse;
import com.assignment3.project.dto.responses.UserResponse;
import com.assignment3.project.entities.User;
import com.assignment3.project.enums.UserRole;
import com.assignment3.project.mappers.UserMapper;
import com.assignment3.project.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public LoginResponse authenticate(LoginRequest input) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.email(), input.password())
        );
        // Principal is UserDetails (Spring Security User), not our User entity
        // Get email from principal and load actual User entity from database
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));
        String token = tokenService.generateToken(authentication);
        return new LoginResponse(token, user.getEmail());
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        if (request.role() == UserRole.DONOR) {
            user.setVerified(true);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}

