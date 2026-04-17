package org.ukhanov.userbase.manager.profile;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.ukhanov.userbase.user.security.CustomUserDetails;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String profile(Model model,
                          Authentication authentication,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          HttpSession httpSession) {

        profileService.addProfileDataToModel(model, authentication, userDetails, httpSession);
        return "auth/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "auth/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 Model model) {

        profileService.changePassword(
                authentication.getName(),
                oldPassword,
                newPassword,
                confirmPassword
        );

        return "redirect:/profile?passwordChanged";
    }
}
