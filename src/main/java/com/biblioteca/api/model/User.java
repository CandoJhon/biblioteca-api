package com.biblioteca.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "the name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "the last name is required")
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "registration_date")
    private LocalDate registryDate = LocalDate.now();

    @Column(nullable = false)
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        if (registryDate == null) {
            registryDate = LocalDate.now();
        }
        if (active == null) {
            active = true;
        }
    }
}