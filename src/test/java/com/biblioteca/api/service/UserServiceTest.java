package com.biblioteca.api.service;

import com.biblioteca.api.model.User;
import com.biblioteca.api.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Jhon");
        testUser.setLastName("Cando");
        testUser.setEmail("john_jm@outlook.com");
        testUser.setPhone("555-1234");
        testUser.setRegistryDate(LocalDate.now());
        testUser.setActive(true);
    }

    @Test
    @DisplayName("Should return all users")
    void testFindAll() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jhon", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find user by ID when exists")
    void testFindByIdExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("john_jm@outlook.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user ID does not exist")
    void testFindByIdNotExists() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find active users")
    void testFindActive() {
        // Given
        when(userRepository.findByActiveTrue()).thenReturn(Arrays.asList(testUser));

        // When
        List<User> result = userService.findActive();

        // Then
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getActive());
        verify(userRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        // Given
        when(userRepository.findByEmail("john_jm@outlook.com"))
                .thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail("john_jm@outlook.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Jhon", result.get().getName());
        verify(userRepository, times(1)).findByEmail("john_jm@outlook.com");
    }

    @Test
    @DisplayName("Should save user with unique email")
    void testSaveUserSuccess() {
        // Given
        when(userRepository.findByEmail("john_jm@outlook.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.save(testUser);

        // Then
        assertNotNull(result);
        assertEquals("john_jm@outlook.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("john_jm@outlook.com");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testSaveUserDuplicateEmail() {
        // Given
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("john_jm@outlook.com");

        when(userRepository.findByEmail("john_jm@outlook.com"))
                .thenReturn(Optional.of(existingUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.save(testUser)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("john_jm@outlook.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        // Given
        User updatedUser = new User();
        updatedUser.setName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("jane.smith@example.com");
        updatedUser.setPhone("555-5678");
        updatedUser.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.update(1L, updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("Jane", result.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateNonExistentUser() {
        // Given
        User updatedUser = new User();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> userService.update(999L, updatedUser));

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> userService.delete(1L));

        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteNonExistentUser() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.delete(999L)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should search users by name")
    void testSearchByName() {
        // Given
        when(userRepository.findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                anyString(), anyString()))
                .thenReturn(Arrays.asList(testUser));

        // When
        List<User> result = userService.searchByName("Jhon");

        // Then
        assertFalse(result.isEmpty());
        assertEquals("Jhon", result.get(0).getName());
        verify(userRepository, times(1))
                .findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(anyString(), anyString());
    }
}