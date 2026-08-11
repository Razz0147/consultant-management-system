package com.cms.controller;

import com.cms.dto.ConsultantDto;
import com.cms.service.ConsultantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsultantController.class)
class ConsultantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultantService consultantService;

    private ConsultantDto consultantDto;

    @BeforeEach
    void setUp() {
        consultantDto = ConsultantDto.builder()
                .id(1L)
                .name("Jane Smith")
                .email("jane.smith@email.com")
                .phone("+1 555-0102")
                .technology("Angular, Java")
                .experience(4)
                .status("Active")
                .build();
    }

    @Test
    @DisplayName("GET /consultants should render list view with consultants")
    void testListConsultants() throws Exception {
        when(consultantService.getConsultants(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(consultantDto)));

        mockMvc.perform(get("/consultants"))
                .andExpect(status().isOk())
                .andExpect(view().name("consultants/list"))
                .andExpect(model().attributeExists("consultants"))
                .andExpect(model().attributeExists("currentPage"));
    }

    @Test
    @DisplayName("GET /consultants/add should render empty form")
    void testShowAddForm() throws Exception {
        mockMvc.perform(get("/consultants/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("consultants/form"))
                .andExpect(model().attributeExists("consultant"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("POST /consultants/save with valid data should redirect to list")
    void testSaveConsultant_Success() throws Exception {
        when(consultantService.createConsultant(any(ConsultantDto.class))).thenReturn(consultantDto);

        mockMvc.perform(post("/consultants/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Jane Smith")
                        .param("email", "jane.smith@email.com")
                        .param("phone", "+1 555-0102")
                        .param("technology", "Angular, Java")
                        .param("experience", "4")
                        .param("status", "Active"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consultants"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /consultants/save with invalid email should return form with validation error")
    void testSaveConsultant_ValidationError() throws Exception {
        mockMvc.perform(post("/consultants/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "") // Blank name
                        .param("email", "invalid-email")
                        .param("phone", "123")
                        .param("technology", "")
                        .param("experience", "-5")
                        .param("status", "Active"))
                .andExpect(status().isOk())
                .andExpect(view().name("consultants/form"))
                .andExpect(model().attributeHasFieldErrors("consultant", "name", "email", "technology", "experience"));
    }
}
