package org.example.atharvolunteeringplatform.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class MyUser implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Username cannot be null")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Column(columnDefinition = "varchar(30) not null UNIQUE")
    private String username;

    @NotEmpty(message = "Please enter the student name")
    @Size(min = 2, max = 50, message = "Student name must be between 2 and 50 characters")
    @Column(columnDefinition = "varchar(50) not null")
    private String name;

    @NotEmpty(message = "Please enter an email")
    @Email(message = "Invalid email format")
    @Column(columnDefinition = "varchar(100) not null unique")
    private String email;

    @NotEmpty(message = "Please enter a phone number")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and be 10 digits long")
    @Column(columnDefinition = "varchar(10) not null unique")
    private String phone_number;

    @NotEmpty(message = "Please enter a password")
    @Column(columnDefinition = "varchar(255) not null")
    private String password;

    @NotEmpty(message = "Please select a role")
    @Pattern(regexp = "^(student|supervisor|organization|admin)$", message = "Role must be one of: student, supervisor, organization, admin")
    @Column(columnDefinition = "varchar(20) not null")
    private String role;


    private LocalDateTime created_at;


    @OneToOne(cascade = CascadeType.ALL,mappedBy = "userStudent")
    @PrimaryKeyJoinColumn
    private Student userStudent;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private Organization organization;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "myUser")
    @PrimaryKeyJoinColumn
    private School school;




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
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