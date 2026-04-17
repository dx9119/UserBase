package org.ukhanov.userbase.manager.state.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.ukhanov.userbase.manager.state.service.UserStateHistoryService;
import org.ukhanov.userbase.manager.state.model.UserStateHistory;

@Controller
@RequestMapping("/admin")
public class UserStateController {

    private final UserStateHistoryService userStateHistoryService;

    public UserStateController(UserStateHistoryService userStateHistoryService) {
        this.userStateHistoryService = userStateHistoryService;
    }

    @GetMapping("/history/{id}")
    public String history(@PathVariable Long id, Model model, @PageableDefault(size = 10) Pageable pageable){
        Page<UserStateHistory> history = userStateHistoryService.getHistory(id, pageable);
        model.addAttribute("history", history);
        return "auth/admin/history";
    }
}
