package com.biblioteca.api.controller;

import com.biblioteca.api.model.Loans;
import com.biblioteca.api.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public ResponseEntity<List<Loans>> getAllLoans() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loans> getLoanById(@PathVariable Long id) {
        return loanService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Loans>> getActiveLoans() {
        return ResponseEntity.ok(loanService.findActiveLoans());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Loans>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.findOverdueLoans());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loans>> getLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.findByUser(userId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Loans>> getLoansByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(loanService.findByBook(bookId));
    }

    @PostMapping
    public ResponseEntity<Loans> createLoan(@Valid @RequestBody Loans loan) {
        try {
            Loans created = loanService.createLoan(loan);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Loans> returnBook(@PathVariable Long id) {
        try {
            Loans returned = loanService.returnBook(id);
            return ResponseEntity.ok(returned);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}