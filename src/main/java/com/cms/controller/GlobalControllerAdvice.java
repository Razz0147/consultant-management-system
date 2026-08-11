package com.cms.controller;

import com.cms.model.ActivityLog;
import com.cms.service.ConsultantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final ConsultantService consultantService;

    @Autowired
    public GlobalControllerAdvice(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @ModelAttribute("currentRole")
    public String currentRole(HttpSession session) {
        String role = (String) session.getAttribute("currentRole");
        if (role == null) {
            role = "Administrator"; // Default simulated role
            session.setAttribute("currentRole", role);
        }
        return role;
    }

    @ModelAttribute("notifications")
    public List<ActivityLog> getNotifications() {
        try {
            return consultantService.getRecentActivities();
        } catch (Exception e) {
            return List.of();
        }
    }
}
