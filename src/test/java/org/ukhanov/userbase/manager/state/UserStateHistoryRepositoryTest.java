package org.ukhanov.userbase.manager.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.ukhanov.userbase.manager.state.model.UserState;
import org.ukhanov.userbase.manager.state.model.UserStateHistory;
import org.ukhanov.userbase.manager.state.repsitory.UserStateHistoryRepository;
import org.ukhanov.userbase.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserStateHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserStateHistoryRepository repository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser = entityManager.persistAndFlush(testUser);
    }

    @Test
    void findAllByChanged_IdOrderByChangedAtDesc_withPagination_returnsPage() {
        for (int i = 0; i < 5; i++) {
            UserStateHistory history = new UserStateHistory();
            history.setChanged(testUser);
            history.setUserState(UserState.PAUSED);
            history.setComment("State " + i);
            entityManager.persistAndFlush(history);
        }

        Pageable pageable = PageRequest.of(0, 3);
        Page<UserStateHistory> result = repository.findAllByChanged_IdOrderByChangedAtDesc(testUser.getId(), pageable);

        assertEquals(3, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.isLast());
    }

    @Test
    void findAllByChanged_IdOrderByChangedAtDesc_secondPage_returnsCorrectData() {
        for (int i = 0; i < 5; i++) {
            UserStateHistory history = new UserStateHistory();
            history.setChanged(testUser);
            history.setUserState(UserState.PAUSED);
            history.setComment("State " + i);
            entityManager.persistAndFlush(history);
        }

        Pageable pageable = PageRequest.of(1, 3);
        Page<UserStateHistory> result = repository.findAllByChanged_IdOrderByChangedAtDesc(testUser.getId(), pageable);

        assertEquals(2, result.getContent().size());
        assertTrue(result.isLast());
    }

    @Test
    void findAllByChanged_IdOrderByChangedAtDesc_noRecords_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserStateHistory> result = repository.findAllByChanged_IdOrderByChangedAtDesc(testUser.getId(), pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void findAllByChanged_IdOrderByChangedAtDesc_nonExistentUser_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserStateHistory> result = repository.findAllByChanged_IdOrderByChangedAtDesc(999L, pageable);

        assertTrue(result.getContent().isEmpty());
    }
}