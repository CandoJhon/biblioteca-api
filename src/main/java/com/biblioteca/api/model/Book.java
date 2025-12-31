package com.biblioteca.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "author is required")
    @Column(nullable = false, length = 150)
    private String author;

    @Column(unique = true, length = 20)
    private String isbn;

    @NotNull(message = "the year publication is required")
    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(length = 100)
    private String editorial;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(name = "registration_date")
    private LocalDate registryDate = LocalDate.now();

    @PrePersist
    protected void onCreate() {
        if (registryDate == null) {
            registryDate = LocalDate.now();
        }
        if (available == null) {
            available = true;
        }
    }
}