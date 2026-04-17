package org.ukhanov.userbase.config;


import org.ukhanov.userbase.user.model.Role;
import org.ukhanov.userbase.user.model.User;
import org.ukhanov.userbase.user.repository.UserRepository;
import org.ukhanov.userbase.manager.state.model.UserState;
import org.ukhanov.userbase.manager.state.model.UserStateHistory;
import org.ukhanov.userbase.manager.state.repsitory.UserStateHistoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, 
                                           UserStateHistoryRepository historyRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN);
                admin = userRepository.save(admin);

                UserStateHistory adminHistory = new UserStateHistory();
                adminHistory.setChanged(admin);
                adminHistory.setChangeAuthor(admin);
                adminHistory.setUserState(UserState.AUTO_CREATED);
                adminHistory.setComment("Создан искусственно");
                adminHistory.setChangedAt(LocalDateTime.now());
                historyRepository.save(adminHistory);

                String[] users = {"user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8", "user9", "user10",
                             "user11", "user12", "user13", "user14", "user15", "user16", "user17", "user18", "user19", "user20"};
                for (String username : users) {
                    User user = new User(username, passwordEncoder.encode("password"), Role.USER);
                    user = userRepository.save(user);

                    UserStateHistory history = new UserStateHistory();
                    history.setChanged(user);
                    history.setChangeAuthor(admin);
                    history.setUserState(UserState.AUTO_CREATED);
                    history.setComment("Создан искусственно");
                    history.setChangedAt(LocalDateTime.now());
                    historyRepository.save(history);
                }
            }
        };
    }
}