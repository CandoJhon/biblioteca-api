package com.biblioteca.api.service;

import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Loans;
import com.biblioteca.api.model.Loans.LoanStatus;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.LoansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private final LoansRepository loanRepository;
    private final BookRepository bookRepository;

    public List<Loans> findAll() {
        return loanRepository.findAll();
    }

    public Optional<Loans> findById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loans> findByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loans> findByBook(Long bookId) {
        return loanRepository.findByBookId(bookId);
    }

    public List<Loans> findActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE);
    }

    public List<Loans> findOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now());
    }

    @Transactional
    public Loans createLoan(Loans loan) {
        // Verify book as available
        Book book = loan.getBook();
        if (book == null || !book.getAvailable()) {
            throw new IllegalArgumentException("Book is not available for loan");
        }

        // Verify there are no active loans books
        Long activeLoans = loanRepository.countActiveLoansByBookId(book.getId());
        if (activeLoans > 0) {
            throw new IllegalArgumentException("Book already has an active loan");
        }

        // Mark book as unavailable
        book.setAvailable(false);
        bookRepository.save(book);

        // Create Loan
        return loanRepository.save(loan);
    }

    @Transactional
    public Loans returnBook(Long loanId) {
        Loans loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanId));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalArgumentException("Loan is not active");
        }

        // Update Loan
        loan.setActualReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        // Mark book as available
        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public void updateOverdueLoans() {
        List<Loans> overdueLoans = findOverdueLoans();
        for (Loans loan : overdueLoans) {
            if (loan.getStatus() == LoanStatus.ACTIVE) {
                loan.setStatus(LoanStatus.EXPIRED);
                loanRepository.save(loan);
            }
        }
    }
}