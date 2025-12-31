package com.biblioteca.api.service;

import com.biblioteca.api.model.Book;
import com.biblioteca.api.model.Loans;
import com.biblioteca.api.model.Loans.LoanStatus;
import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.BookRepository;
import com.biblioteca.api.repository.LoansRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoansRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LoanService loanService;

    private Loans testLoan;
    private Book testBook;
    private User testUser;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Clean Code");
        testBook.setAuthor("Robert Martin");
        testBook.setAvailable(true);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Jhon");
        testUser.setLastName("Cando");
        testUser.setEmail("john_jm@outlook.com");

        testLoan = new Loans();
        testLoan.setId(1L);
        testLoan.setBook(testBook);
        testLoan.setUser(testUser);
        testLoan.setLoanDate(LocalDate.now());
        testLoan.setExpectReturnDate(LocalDate.now().plusDays(14));
        testLoan.setStatus(LoanStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return all loans")
    void testFindAll() {
        // Given
        List<Loans> loans = Arrays.asList(testLoan);
        when(loanRepository.findAll()).thenReturn(loans);

        // When
        List<Loans> result = loanService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find loan by ID")
    void testFindById() {
        // Given
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        // When
        Optional<Loans> result = loanService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(LoanStatus.ACTIVE, result.get().getStatus());
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should find loans by user")
    void testFindByUser() {
        // Given
        when(loanRepository.findByUserId(1L)).thenReturn(Arrays.asList(testLoan));

        // When
        List<Loans> result = loanService.findByUser(1L);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getUser().getId());
        verify(loanRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should find loans by book")
    void testFindByBook() {
        // Given
        when(loanRepository.findByBookId(1L)).thenReturn(Arrays.asList(testLoan));

        // When
        List<Loans> result = loanService.findByBook(1L);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getBook().getId());
        verify(loanRepository, times(1)).findByBookId(1L);
    }

    @Test
    @DisplayName("Should find active loans")
    void testFindActiveLoans() {
        // Given
        when(loanRepository.findByStatus(LoanStatus.ACTIVE))
                .thenReturn(Arrays.asList(testLoan));

        // When
        List<Loans> result = loanService.findActiveLoans();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(LoanStatus.ACTIVE, result.get(0).getStatus());
        verify(loanRepository, times(1)).findByStatus(LoanStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find overdue loans")
    void testFindOverdueLoans() {
        // Given
        testLoan.setExpectReturnDate(LocalDate.now().minusDays(1));
        when(loanRepository.findOverdueLoans(any(LocalDate.class)))
                .thenReturn(Arrays.asList(testLoan));

        // When
        List<Loans> result = loanService.findOverdueLoans();

        // Then
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getExpectReturnDate().isBefore(LocalDate.now()));
        verify(loanRepository, times(1)).findOverdueLoans(any(LocalDate.class));
    }

    @Test
    @DisplayName("Should create loan successfully when book is available")
    void testCreateLoanSuccess() {
        // Given
        when(loanRepository.countActiveLoansByBookId(1L)).thenReturn(0L);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(loanRepository.save(any(Loans.class))).thenReturn(testLoan);

        // When
        Loans result = loanService.createLoan(testLoan);

        // Then
        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        verify(loanRepository, times(1)).countActiveLoansByBookId(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(loanRepository, times(1)).save(testLoan);
    }

    @Test
    @DisplayName("Should throw exception when book is not available")
    void testCreateLoanBookNotAvailable() {
        // Given
        testBook.setAvailable(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(testLoan)
        );

        assertTrue(exception.getMessage().contains("not available"));
        verify(loanRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Should throw exception when book is null")
    void testCreateLoanBookNull() {
        // Given
        testLoan.setBook(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(testLoan)
        );

        assertTrue(exception.getMessage().contains("not available"));
        verify(loanRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Should throw exception when book already has active loan")
    void testCreateLoanBookAlreadyLoaned() {
        // Given
        when(loanRepository.countActiveLoansByBookId(1L)).thenReturn(1L);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(testLoan)
        );

        assertTrue(exception.getMessage().contains("already has an active loan"));
        verify(loanRepository, times(1)).countActiveLoansByBookId(1L);
        verify(loanRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Should return book successfully")
    void testReturnBookSuccess() {
        // Given
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(loanRepository.save(any(Loans.class))).thenReturn(testLoan);

        // When
        Loans result = loanService.returnBook(1L);

        // Then
        assertNotNull(result);
        assertEquals(LoanStatus.RETURNED, result.getStatus());
        assertNotNull(result.getActualReturnDate());
        verify(loanRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(loanRepository, times(1)).save(any(Loans.class));
    }

    @Test
    @DisplayName("Should throw exception when returning non-existent loan")
    void testReturnNonExistentLoan() {
        // Given
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.returnBook(999L)
        );

        assertTrue(exception.getMessage().contains("Loan not found"));
        verify(loanRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when loan is not active")
    void testReturnInactiveLoan() {
        // Given
        testLoan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.returnBook(1L)
        );

        assertTrue(exception.getMessage().contains("not active"));
        verify(loanRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should update overdue loans")
    void testUpdateOverdueLoans() {
        // Given
        Loans overdueLoan = new Loans();
        overdueLoan.setId(1L);
        overdueLoan.setStatus(LoanStatus.ACTIVE);
        overdueLoan.setExpectReturnDate(LocalDate.now().minusDays(5));

        when(loanRepository.findOverdueLoans(any(LocalDate.class)))
                .thenReturn(Arrays.asList(overdueLoan));
        when(loanRepository.save(any(Loans.class))).thenReturn(overdueLoan);

        // When
        loanService.updateOverdueLoans();

        // Then
        verify(loanRepository, times(1)).findOverdueLoans(any(LocalDate.class));
        verify(loanRepository, times(1)).save(any(Loans.class));
    }
}