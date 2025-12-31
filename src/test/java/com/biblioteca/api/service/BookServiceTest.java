package com.biblioteca.api.service;

import com.biblioteca.api.model.Book;
import com.biblioteca.api.repository.BookRepository;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Clean Code");
        testBook.setAuthor("Robert Martin");
        testBook.setIsbn("978-0132350884");
        testBook.setPublicationYear(2008);
        testBook.setEditorial("Prentice Hall");
        testBook.setAvailable(true);
        testBook.setRegistryDate(LocalDate.now());
    }

    @Test
    @DisplayName("Should return all books")
    void testFindAll() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<Book> result = bookService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find book by ID when exists")
    void testFindByIdExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        Optional<Book> result = bookService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Clean Code", result.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when book ID does not exist")
    void testFindByIdNotExists() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find available books")
    void testFindAvailable() {
        // Given
        when(bookRepository.findByAvailableTrue()).thenReturn(Arrays.asList(testBook));

        // When
        List<Book> result = bookService.findAvailable();

        // Then
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getAvailable());
        verify(bookRepository, times(1)).findByAvailableTrue();
    }

    @Test
    @DisplayName("Should save book with unique ISBN")
    void testSaveBookSuccess() {
        // Given
        when(bookRepository.findByIsbn("978-0132350884")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = bookService.save(testBook);

        // Then
        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        verify(bookRepository, times(1)).findByIsbn("978-0132350884");
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    @DisplayName("Should throw exception when ISBN already exists")
    void testSaveBookDuplicateISBN() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(2L);
        existingBook.setIsbn("978-0132350884");

        when(bookRepository.findByIsbn("978-0132350884"))
                .thenReturn(Optional.of(existingBook));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.save(testBook)
        );

        assertEquals("ISBN already exists", exception.getMessage());
        verify(bookRepository, times(1)).findByIsbn("978-0132350884");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should update book successfully")
    void testUpdateBook() {
        // Given
        Book updatedBook = new Book();
        updatedBook.setTitle("Clean Code Updated");
        updatedBook.setAuthor("Robert C. Martin");
        updatedBook.setIsbn("978-0132350884");
        updatedBook.setPublicationYear(2009);
        updatedBook.setAvailable(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // When
        Book result = bookService.update(1L, updatedBook);

        // Then
        assertNotNull(result);
        assertEquals("Clean Code Updated", result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent book")
    void testUpdateNonExistentBook() {
        // Given
        Book updatedBook = new Book();
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> bookService.update(999L, updatedBook));

        verify(bookRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should delete book successfully")
    void testDeleteBook() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> bookService.delete(1L));

        // Then
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent book")
    void testDeleteNonExistentBook() {
        // Given
        when(bookRepository.existsById(999L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.delete(999L)
        );

        assertTrue(exception.getMessage().contains("Book not found"));
        verify(bookRepository, times(1)).existsById(999L);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should search books by title")
    void testSearchByTitle() {
        // Given
        when(bookRepository.findByTitleContainingIgnoreCase("Clean"))
                .thenReturn(Arrays.asList(testBook));

        // When
        List<Book> result = bookService.searchByTitle("Clean");

        // Then
        assertFalse(result.isEmpty());
        assertEquals("Clean Code", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("Clean");
    }

    @Test
    @DisplayName("Should search books by author")
    void testSearchByAuthor() {
        // Given
        when(bookRepository.findByAuthorContainingIgnoreCase("Martin"))
                .thenReturn(Arrays.asList(testBook));

        // When
        List<Book> result = bookService.searchByAuthor("Martin");

        // Then
        assertFalse(result.isEmpty());
        assertEquals("Robert Martin", result.get(0).getAuthor());
        verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase("Martin");
    }
}