package com.project.demo.models;

import com.project.demo.models.Enumerations.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_account")
public class UserAccount implements UserDetails, Principal {
    @Column(length = 1000)
    String image;

    @Column()
    String phone;
    boolean isVerified = true;
    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column()
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private boolean available;


    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return email;
    }
}




