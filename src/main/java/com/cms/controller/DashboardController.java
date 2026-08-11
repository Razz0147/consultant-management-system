package com.cms.controller;

import com.cms.dto.ConsultantDto;
import com.cms.dto.ConsultantStatsDto;
import com.cms.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ConsultantService consultantService;

    @Autowired
    public DashboardController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping({"/", "/dashboard"})
    public String viewDashboard(Model model) {
        ConsultantStatsDto stats = consultantService.getDashboardStats();
        List<ConsultantDto> allConsultants = consultantService.getAllConsultants();
        
        // Pick latest 5 consultants for recent activity widget
        List<ConsultantDto> recentConsultants = allConsultants.stream()
                .limit(5)
                .toList();

        model.addAttribute("stats", stats);
        model.addAttribute("recentConsultants", recentConsultants);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
