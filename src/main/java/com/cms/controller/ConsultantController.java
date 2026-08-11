package com.cms.controller;

import com.cms.dto.ConsultantDto;
import com.cms.service.ConsultantService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/consultants")
public class ConsultantController {

    private final ConsultantService consultantService;

    @Autowired
    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    private String getSessionRole(HttpSession session) {
        String role = (String) session.getAttribute("currentRole");
        return role != null ? role : "Administrator";
    }

    @GetMapping
    public String listConsultants(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false, defaultValue = "") String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortField).ascending() 
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConsultantDto> consultantPage = consultantService.getConsultants(keyword, status, pageable);

        model.addAttribute("consultants", consultantPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", consultantPage.getTotalPages());
        model.addAttribute("totalItems", consultantPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "consultants");

        return "consultants/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String role = getSessionRole(session);
        if ("Viewer".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Viewers cannot create consultant profiles.");
            return "redirect:/consultants";
        }
        
        ConsultantDto consultantDto = new ConsultantDto();
        consultantDto.setStatus("Available");
        model.addAttribute("consultant", consultantDto);
        model.addAttribute("pageTitle", "Add New Consultant");
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "add-consultant");
        return "consultants/form";
    }

    @PostMapping("/save")
    public String saveConsultant(
            @Valid @ModelAttribute("consultant") ConsultantDto consultantDto,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String role = getSessionRole(session);
        if ("Viewer".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Viewers cannot save consultant profiles.");
            return "redirect:/consultants";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", consultantDto.getId() == null ? "Add New Consultant" : "Edit Consultant");
            model.addAttribute("isEdit", consultantDto.getId() != null);
            model.addAttribute("activePage", consultantDto.getId() == null ? "add-consultant" : "consultants");
            return "consultants/form";
        }

        try {
            if (consultantDto.getId() == null) {
                consultantService.createConsultant(consultantDto);
                redirectAttributes.addFlashAttribute("successMessage", "Consultant '" + consultantDto.getName() + "' successfully created!");
            } else {
                consultantService.updateConsultant(consultantDto.getId(), consultantDto);
                redirectAttributes.addFlashAttribute("successMessage", "Consultant '" + consultantDto.getName() + "' successfully updated!");
            }
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("email", "error.consultant", ex.getMessage());
            model.addAttribute("pageTitle", consultantDto.getId() == null ? "Add New Consultant" : "Edit Consultant");
            model.addAttribute("isEdit", consultantDto.getId() != null);
            model.addAttribute("activePage", consultantDto.getId() == null ? "add-consultant" : "consultants");
            return "consultants/form";
        }

        return "redirect:/consultants";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String role = getSessionRole(session);
        if ("Viewer".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Viewers cannot edit consultant profiles.");
            return "redirect:/consultants";
        }

        try {
            ConsultantDto consultantDto = consultantService.getConsultantById(id);
            model.addAttribute("consultant", consultantDto);
            model.addAttribute("pageTitle", "Edit Consultant - " + consultantDto.getName());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "consultants");
            return "consultants/form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/consultants";
        }
    }

    @GetMapping("/view/{id}")
    public String viewConsultant(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ConsultantDto consultantDto = consultantService.getConsultantById(id);
            model.addAttribute("consultant", consultantDto);
            model.addAttribute("activePage", "consultants");
            return "consultants/view";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/consultants";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteConsultant(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String role = getSessionRole(session);
        if (!"Administrator".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Only Administrators can delete consultant records.");
            return "redirect:/consultants";
        }

        try {
            ConsultantDto dto = consultantService.getConsultantById(id);
            consultantService.deleteConsultant(id);
            redirectAttributes.addFlashAttribute("successMessage", "Consultant '" + dto.getName() + "' was successfully deleted.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/consultants";
    }
}
