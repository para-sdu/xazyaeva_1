package com.assignment3.project.services;

import com.assignment3.project.dto.responses.UserResponse;
import com.assignment3.project.entities.User;
import com.assignment3.project.enums.UserRole;
import com.assignment3.project.mappers.UserMapper;
import com.assignment3.project.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("UserService.loadUserByUsername called email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String authority;
        if (user.getRole() == UserRole.ADMIN) {
            authority = "ROLE_ADMIN";
        } else if (user.getRole() == UserRole.DONOR) {
            authority = "ROLE_DONOR";
        } else if (user.getRole() == UserRole.NEEDS_HELP) {
            authority = "ROLE_NEEDS_HELP";
        } else {
            authority = "ROLE_USER";
        }
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public UserResponse getUserByEmail(String email) {
        log.info("UserService.getUserByEmail called email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public List<UserResponse> getAllUsers() {
        log.info("UserService.getAllUsers called");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserResponse createUser(String fullName,
                                   String email,
                                   String password,
                                   UserRole role,
                                   MultipartFile avatar,
                                   MultipartFile document) throws IOException {
        log.info("UserService.createUser called email={} role={} avatarPresent={} documentPresent={}", 
                email, role, avatar != null && !avatar.isEmpty(), document != null && !document.isEmpty());
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        if (role == UserRole.DONOR) {
            user.setVerified(true);
        }
        if (avatar != null && !avatar.isEmpty()) {
            String path = fileStorageService.saveAvatar(avatar);
            user.setAvatarPath(path);
        }
        if (document != null && !document.isEmpty()) {
            String path = fileStorageService.saveDocument(document);
            user.setDocPath(path);
        }
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id,
                                   String fullName,
                                   String email,
                                   String password,
                                   UserRole role,
                                   MultipartFile avatar,
                                   MultipartFile document) throws IOException {
        log.info("UserService.updateUser called id={}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(fullName != null)
            user.setFullName(fullName);
        if(email != null)
            user.setEmail(email);
        if(password != null)
            user.setPassword(passwordEncoder.encode(password));
        if(role != null)
            user.setRole(role);
        if (avatar != null && !avatar.isEmpty()) {
            String path = fileStorageService.saveAvatar(avatar);
            user.setAvatarPath(path);
        }
        if (document != null && !document.isEmpty()) {
            String path = fileStorageService.saveDocument(document);
            user.setDocPath(path);
        }
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("UserService.deleteUser called id={}", id);
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("User not found");
        }
        userRepository.deleteById(id);
        log.info("UserService.deleteUser success id={}", id);
    }

    public Set<User> getUsersByIds(Set<Long> ids) {
        log.info("UserService.getUsersByIds called count={}", ids != null ? ids.size() : 0);
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(userRepository.findAllById(ids));
    }

    public User getUserByIdOrThrow(Long id) {
        log.info("UserService.getUserByIdOrThrow called id={}", id);
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public UserResponse verifyUser(Long id) {
        log.info("UserService.verifyUser called id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        user.setVerified(true);
        User saved = userRepository.save(user);
        log.info("User with id {} verified", id);
        return userMapper.toDto(saved);
    }

    public User getCurrentUser() {
        var token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var email = (String) token.getToken().getClaim("email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}