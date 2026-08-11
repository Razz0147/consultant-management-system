package com.cms.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoleController {

    @GetMapping("/switch-role")
    public String switchRole(@RequestParam String role, HttpSession session, HttpServletRequest request) {
        if ("Administrator".equalsIgnoreCase(role) || "Manager".equalsIgnoreCase(role) || "Viewer".equalsIgnoreCase(role)) {
            session.setAttribute("currentRole", role);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null && !referer.isEmpty() ? referer : "/dashboard");
    }
}
