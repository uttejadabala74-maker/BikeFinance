package com.bikefinance.controller;

import com.bikefinance.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final ReportService reportService;

    public DashboardController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Map<String, Object> stats = reportService.getDashboardStats();
        model.addAllAttributes(stats);
        model.addAttribute("activeTab", "dashboard");
        return "dashboard";
    }
}
