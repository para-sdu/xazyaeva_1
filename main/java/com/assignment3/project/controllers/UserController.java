package com.assignment3.project.controllers;

import com.assignment3.project.dto.requests.UserCreateRequest;
import com.assignment3.project.dto.requests.UserUpdateRequest;
import com.assignment3.project.dto.responses.UserResponse;
import com.assignment3.project.entities.User;
import com.assignment3.project.enums.UserRole;
import com.assignment3.project.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        var token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var email = (String) token.getToken().getClaim("email");
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<UserResponse> createUser(
            @ModelAttribute @Valid UserCreateRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "document", required = false) MultipartFile document
    ) throws IOException {
        UserResponse created = userService.createUser(
                request.getFullName(), 
                request.getEmail(), 
                request.getPassword(), 
                request.getRole(), 
                avatar, 
                document
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(path = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @ModelAttribute @Valid UserUpdateRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "document", required = false) MultipartFile document
    ) throws IOException {
        UserResponse updated = userService.updateUser(
                id, 
                request.getFullName(), 
                request.getEmail(), 
                request.getPassword(), 
                request.getRole(), 
                avatar, 
                document
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<UserResponse> verifyUser(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only administrators can verify users");
        }
        
        UserResponse verifiedUser = userService.verifyUser(id);
        return ResponseEntity.ok(verifiedUser);
    }
}
