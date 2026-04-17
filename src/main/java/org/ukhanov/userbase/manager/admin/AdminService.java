package org.ukhanov.userbase.manager.admin;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.ukhanov.userbase.user.model.Role;
import org.ukhanov.userbase.user.model.User;
import org.ukhanov.userbase.user.repository.UserRepository;
import org.ukhanov.userbase.exception.EmptyReasonException;
import org.ukhanov.userbase.exception.RoleAlreadyExistsException;
import org.ukhanov.userbase.manager.state.model.UserState;
import org.ukhanov.userbase.manager.state.service.UserStateHistoryService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final UserStateHistoryService userStateHistoryService;
    private final MessageSource messageSource;

    public AdminService(UserRepository userRepository, UserStateHistoryService userStateHistoryService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.userStateHistoryService = userStateHistoryService;
        this.messageSource = messageSource;
    }

    public Page<User> getUsersPage(int page, int size) {
        logger.debug("Fetching users page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page,
                                           size,
                                           Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }

    public User getUserForEdit(Long id) {
        logger.debug("Fetching user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    public Set<Role> getAllRoles() {
        return Arrays.stream(Role.values()).collect(Collectors.toSet());
    }

    @Transactional
    public void updateUser(Long id, Set<Role> roles, String reason) {
        logger.info("Updating user id={}, roles={}, reason={}", id, roles, reason);
        validateReason(reason);
        User user = userRepository.findById(id).orElseThrow();
        Set<Role> currentRoles = user.getRoles();

        for (Role newRole : roles) {
            if (currentRoles.contains(newRole)) {
                Locale locale = LocaleContextHolder.getLocale();
                String message = messageSource.getMessage("admin.role.exists", new Object[]{newRole}, locale);
                throw new RoleAlreadyExistsException(message);
            }
        }

        User changeAuthor = getCurrentUser();
        user.setRoles(roles);
        userRepository.save(user);
        userStateHistoryService.saveUserHistory(user, changeAuthor, UserState.UPDATED, reason);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    @Transactional
    public void disableUser(Long id, String reason) {
        logger.info("Disabling user id={}, reason={}", id, reason);
        validateReason(reason);

        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(false);
        userRepository.save(user);

        User changeAuthor = getCurrentUser();
        userStateHistoryService.saveUserHistory(user, changeAuthor, UserState.DISABLED, reason);
    }

    @Transactional
    public void enableUser(Long id, String reason) {
        logger.info("Enabling user id={}, reason={}", id, reason);
        validateReason(reason);

        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(true);
        userRepository.save(user);

        User changeAuthor = getCurrentUser();
        userStateHistoryService.saveUserHistory(user, changeAuthor, UserState.ENABLED, reason);
    }

    private void validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage("admin.reason.empty", null, locale);
            throw new EmptyReasonException(message);
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return userRepository.findByUsername(auth.getName()).orElse(null);
        }
        return null;
    }

    public Page<User> searchUsersPage(int page, int size, String name, LocalDate from, LocalDate to) {
        logger.debug("Searching users: page={}, name='{}', from={}, to={}", page, name, from, to);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Specification<User> spec = Specification
                .where(UserSpecs.hasName(name))
                .and(UserSpecs.createdBetween(from,to));

        return userRepository.findAll(spec, pageable);
    }

}
