package org.ukhanov.userbase.manager.admin;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.ukhanov.userbase.user.model.Role;
import org.ukhanov.userbase.user.model.User;

import java.time.LocalDate;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String admin(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) String from,
                        @RequestParam(required = false) String to,
                        Model model) {
        Page<User> userPage;

        LocalDate fromDate = from != null && !from.isBlank() ? LocalDate.parse(from) : null;
        LocalDate toDate = to != null && !to.isBlank() ? LocalDate.parse(to) : null;

        if ((name != null && !name.isBlank()) || fromDate != null || toDate != null) {
            userPage = adminService.searchUsersPage(page, 6, name, fromDate, toDate);
            model.addAttribute("searchName", name);
            model.addAttribute("searchFrom", from);
            model.addAttribute("searchTo", to);
        } else {
            userPage = adminService.getUsersPage(page, 6);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());

        return "auth/admin/admin";
    }

    @GetMapping("/user/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", adminService.getUserForEdit(id));
        model.addAttribute("allRoles", adminService.getAllRoles());
        return "auth/admin/admin-edit";
    }

    @GetMapping("/roles")
    @ResponseBody
    public Role[] getRoles() {
        return Role.values();
    }

    @PostMapping("/user/{id}/update")
    public String updateUser(@PathVariable Long id,
                             @RequestParam Set<Role> roles,
                             @RequestParam String reason) {
        adminService.updateUser(id, roles, reason);
        return "redirect:/admin?updated";
    }

    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin?deleted";
    }

    @PostMapping("/user/{id}/disable")
    public String disableUser(@PathVariable Long id, @RequestParam String reason) {
        adminService.disableUser(id, reason);
        return "redirect:/admin?disabled";
    }

    @PostMapping("/user/{id}/enable")
    public String enableUser(@PathVariable Long id, @RequestParam String reason) {
        adminService.enableUser(id, reason);
        return "redirect:/admin?enabled";
    }
}
