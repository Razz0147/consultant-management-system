package com.cms.controller;

import com.cms.dto.ConsultantDto;
import com.cms.dto.ConsultantStatsDto;
import com.cms.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ReportsController {

    private final ConsultantService consultantService;

    @Autowired
    public ReportsController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        ConsultantStatsDto stats = consultantService.getDashboardStats();
        List<ConsultantDto> allConsultants = consultantService.getAllConsultants();

        // Calculate average experience by technology stack
        Map<String, Double> avgExpByTech = allConsultants.stream()
                .filter(c -> c.getTechnology() != null && !c.getTechnology().isBlank())
                .collect(Collectors.groupingBy(
                        c -> c.getTechnology().trim(),
                        Collectors.averagingInt(ConsultantDto::getExperience)
                ));

        // Format to 1 decimal place for UI
        Map<String, String> formattedAvgExp = avgExpByTech.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.format("%.1f yrs", e.getValue())
                ));

        model.addAttribute("stats", stats);
        model.addAttribute("avgExpByTech", formattedAvgExp);
        model.addAttribute("activePage", "reports");
        return "reports";
    }
}
