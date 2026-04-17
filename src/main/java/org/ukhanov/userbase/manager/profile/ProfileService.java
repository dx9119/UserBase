package org.ukhanov.userbase.manager.profile;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.ukhanov.userbase.auth.service.AuthService;
import org.ukhanov.userbase.exception.PasswordChangeException;
import org.ukhanov.userbase.user.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private final AuthService authService;
    private final MessageSource messageSource;

    public ProfileService(AuthService authService, MessageSource messageSource) {
        this.authService = authService;
        this.messageSource = messageSource;
    }

    public void addProfileDataToModel(Model model, Authentication authentication,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      HttpSession httpSession) {

        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        model.addAttribute("isAuthenticated", authentication.isAuthenticated());
        model.addAttribute("userId", userDetails.getId());
        model.addAttribute("sessionId", httpSession.getId());
        model.addAttribute("sessionCreated", httpSession.getCreationTime());
        model.addAttribute("sessionLastAccessed", httpSession.getLastAccessedTime());
    }

    public void changePassword(String username, String oldPassword,
                               String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            logger.warn("Password change failed for user {}: passwords don't match", username);
            String msg = messageSource.getMessage("password.dontMath", null, LocaleContextHolder.getLocale());
            throw new PasswordChangeException(msg);
        }

        authService.changePassword(username, oldPassword, newPassword);
        logger.info("Password changed successfully for user: {}", username);
    }
}
