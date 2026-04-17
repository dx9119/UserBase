package org.ukhanov.userbase.manager.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.ukhanov.userbase.user.model.User;
import org.ukhanov.userbase.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(userRepository, null, null);
    }

    @Test
    void searchUsersPage_withValidName_returnsUsersPage() {
        String name = "test";
        int page = 0;
        int size = 10;
        List<User> users = List.of(new User("user1", "pass"), new User("user2", "pass"));
        Page<User> expectedPage = new PageImpl<>(users);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = adminService.searchUsersPage(page, size, name, null, null);

        assertEquals(2, result.getContent().size());
        verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchUsersPage_withNullName_returnsAllUsers() {
        int page = 0;
        int size = 10;
        Page<User> expectedPage = new PageImpl<>(List.of());

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = adminService.searchUsersPage(page, size, null, null, null);

        assertEquals(0, result.getContent().size());
    }

    @Test
    void searchUsersPage_withEmptyName_returnsAllUsers() {
        int page = 0;
        int size = 10;
        Page<User> expectedPage = new PageImpl<>(List.of());

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<User> result = adminService.searchUsersPage(page, size, "", null, null);

        assertEquals(0, result.getContent().size());
    }
}