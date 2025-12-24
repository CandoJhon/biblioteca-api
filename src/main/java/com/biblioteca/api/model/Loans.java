package com.biblioteca.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The book is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Book", nullable = false)
    private Book book;

    @NotNull(message = "Username is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_User", nullable = false)
    private User user;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate = LocalDate.now();

    @Column(name = "expect_return_date", nullable = false)
    private LocalDate ExpectReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status = LoanStatus.ACTIVE;

    public enum LoanStatus {
        ACTIVE,
        RETURNED,
        EXPIRED
    }

    @PrePersist
    protected void onCreate() {
        if (loanDate == null) {
            loanDate = LocalDate.now();
        }
        if (ExpectReturnDate == null) {
            // 14 days by default
            ExpectReturnDate = LocalDate.now().plusDays(14);
        }
        if (status == null) {
            status = LoanStatus.ACTIVE;
        }
    }
}