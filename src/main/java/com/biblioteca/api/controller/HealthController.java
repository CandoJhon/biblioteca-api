package com.biblioteca.api.controller;

import com.biblioteca.api.service.BookService;
import com.biblioteca.api.service.UserService;
import com.biblioteca.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final BookService bookService;
    private final UserService userService;
    private final LoanService loanService;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Biblioteca API");
        response.put("timestamp", LocalDateTime.now());
        response.put("database", "Connected");

        // Estadísticas básicas
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookService.findAll().size());
        stats.put("availableBooks", bookService.findAvailable().size());
        stats.put("totalUsers", userService.findAll().size());
        stats.put("activeLoans", loanService.findActiveLoans().size());

        response.put("stats", stats);

        return response;
    }
}