package com.bikefinance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("businessName", "Sri Sai Teja Finance and Auto Consultancy");
        model.addAttribute("activeTab", "settings");
        return "settings";
    }
}
