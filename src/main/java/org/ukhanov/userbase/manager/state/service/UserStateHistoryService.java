package org.ukhanov.userbase.manager.state.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.ukhanov.userbase.manager.state.model.UserState;
import org.ukhanov.userbase.manager.state.model.UserStateHistory;
import org.ukhanov.userbase.manager.state.repsitory.UserStateHistoryRepository;
import org.ukhanov.userbase.user.model.User;

import java.time.LocalDateTime;

@Service
public class UserStateHistoryService {

    private final UserStateHistoryRepository historyRepository;

    public UserStateHistoryService(UserStateHistoryRepository userStateHistoryRepository) {
        this.historyRepository = userStateHistoryRepository;
    }

    public Page<UserStateHistory> getHistory(Long id, Pageable pageable){
        return historyRepository.findAllByChanged_IdOrderByChangedAtDesc(id, pageable);
    }

    public void saveUserHistory(User changed, User changeAuthor, UserState userState, String msg) {
        UserStateHistory history = new UserStateHistory();
        history.setChanged(changed);
        history.setChangeAuthor(changeAuthor);
        history.setUserState(userState);
        history.setComment(msg);
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

}
