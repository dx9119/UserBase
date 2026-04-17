package org.ukhanov.userbase.auth.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ukhanov.userbase.user.model.Role;
import org.ukhanov.userbase.user.model.User;
import org.ukhanov.userbase.user.repository.UserRepository;
import org.ukhanov.userbase.exception.RegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    public User register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            logger.warn("Registration attempt with existing username: {}", username);
            String message = messageSource.getMessage("register.error", null, LocaleContextHolder.getLocale());
            throw new RegistrationException(message);
        }

        User user = new User(username, passwordEncoder.encode(password), Role.USER);
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }


    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.warn("Failed password change attempt for user: {}", username);
            String message = messageSource.getMessage("password.error_old", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(message);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password changed for user: {}", username);
    }


}