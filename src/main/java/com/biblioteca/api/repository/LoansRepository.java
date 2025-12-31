package com.biblioteca.api.repository;

import com.biblioteca.api.model.Loans;
import com.biblioteca.api.model.Loans.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoansRepository extends JpaRepository<Loans, Long> {

    List<Loans> findByStatus(LoanStatus status);

    List<Loans> findByUserId(Long UserId);

    List<Loans> findByBookId(Long BookId);

    @Query("SELECT p FROM Loans p WHERE p.status = 'ACTIVE' AND p.expectReturnDate < :date")
    List<Loans> findOverdueLoans(@Param("date") LocalDate date);

    @Query("SELECT COUNT(p) FROM Loans p WHERE p.book.id = :bookId AND p.status = 'ACTIVE'")
    Long countActiveLoansByBookId(@Param("bookId") Long BokId);
}
