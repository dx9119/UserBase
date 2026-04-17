package org.ukhanov.userbase.manager.state.model;

import jakarta.persistence.*;
import org.ukhanov.userbase.user.model.User;


import java.time.LocalDateTime;

@Entity
public class UserStateHistory {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserState userState = UserState.NOT_SET;

    private String comment;
    private LocalDateTime changedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User changed;

    @ManyToOne
    @JoinColumn(name = "change_author_id")
    private User changeAuthor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public User getChanged() {
        return changed;
    }

    public void setChanged(User changed) {
        this.changed = changed;
    }

    public User getChangeAuthor() {
        return changeAuthor;
    }

    public void setChangeAuthor(User changeAuthor) {
        this.changeAuthor = changeAuthor;
    }
}

