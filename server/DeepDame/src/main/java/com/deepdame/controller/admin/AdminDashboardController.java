package com.deepdame.controller.admin;

import com.deepdame.service.statistic.StatisticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final StatisticsServiceImpl statisticsService;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("stats", statisticsService.getAdminDashboardStats());
        return "admin/dashboard";
    }
}
