package com.assignment3.project.entities;

import com.assignment3.project.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "avatar_path", nullable = true)
    private String avatarPath;

    @Column(name = "doc_path", nullable = true)
    private String docPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Project> authoredProjects = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority;
        if (role == UserRole.ADMIN) {
            authority = "ROLE_ADMIN";
        } else if (role == UserRole.DONOR) {
            authority = "ROLE_DONOR";
        } else if (role == UserRole.NEEDS_HELP) {
            authority = "ROLE_NEEDS_HELP";
        } else {
            authority = "ROLE_USER";
        }
        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
