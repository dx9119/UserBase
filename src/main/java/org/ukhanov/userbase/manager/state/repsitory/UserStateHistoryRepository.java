package org.ukhanov.userbase.manager.state.repsitory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ukhanov.userbase.manager.state.model.UserStateHistory;

@Repository
public interface UserStateHistoryRepository extends JpaRepository<UserStateHistory, Long> {
    Page<UserStateHistory> findAllByChanged_IdOrderByChangedAtDesc(Long id, Pageable pageable);
}
