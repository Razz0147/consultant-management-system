package com.cms.service;

import com.cms.dto.ConsultantDto;
import com.cms.dto.ConsultantStatsDto;
import com.cms.model.Consultant;
import com.cms.repository.ConsultantRepository;
import com.cms.repository.ActivityLogRepository;
import com.cms.service.impl.ConsultantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultantServiceTest {

    @Mock
    private ConsultantRepository consultantRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @InjectMocks
    private ConsultantServiceImpl consultantService;

    private Consultant consultant;
    private ConsultantDto consultantDto;

    @BeforeEach
    void setUp() {
        consultant = Consultant.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@email.com")
                .phone("+1 555-0101")
                .technology("Java, Spring Boot")
                .experience(5)
                .status("Available")
                .build();

        consultantDto = ConsultantDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@email.com")
                .phone("+1 555-0101")
                .technology("Java, Spring Boot")
                .experience(5)
                .status("Available")
                .build();
    }

    @Test
    @DisplayName("Should return all consultants")
    void testGetAllConsultants() {
        when(consultantRepository.findAll()).thenReturn(List.of(consultant));

        List<ConsultantDto> result = consultantService.getAllConsultants();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        verify(consultantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return consultant by ID")
    void testGetConsultantById_Success() {
        when(consultantRepository.findById(1L)).thenReturn(Optional.of(consultant));

        ConsultantDto result = consultantService.getConsultantById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@email.com");
    }

    @Test
    @DisplayName("Should throw exception when consultant ID not found")
    void testGetConsultantById_NotFound() {
        when(consultantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultantService.getConsultantById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Consultant not found with id: 99");
    }

    @Test
    @DisplayName("Should create consultant successfully")
    void testCreateConsultant_Success() {
        when(consultantRepository.existsByEmail("john.doe@email.com")).thenReturn(false);
        when(consultantRepository.save(any(Consultant.class))).thenReturn(consultant);
        when(activityLogRepository.save(any())).thenReturn(null);

        ConsultantDto created = consultantService.createConsultant(consultantDto);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("John Doe");
        verify(consultantRepository, times(1)).save(any(Consultant.class));
        verify(activityLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating consultant with duplicate email")
    void testCreateConsultant_DuplicateEmail() {
        when(consultantRepository.existsByEmail("john.doe@email.com")).thenReturn(true);

        assertThatThrownBy(() -> consultantService.createConsultant(consultantDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");

        verify(consultantRepository, never()).save(any(Consultant.class));
    }

    @Test
    @DisplayName("Should delete consultant successfully")
    void testDeleteConsultant_Success() {
        when(consultantRepository.findById(1L)).thenReturn(Optional.of(consultant));
        doNothing().when(consultantRepository).delete(any(Consultant.class));
        when(activityLogRepository.save(any())).thenReturn(null);

        consultantService.deleteConsultant(1L);

        verify(consultantRepository, times(1)).delete(any(Consultant.class));
        verify(activityLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return correct dashboard statistics")
    void testGetDashboardStats() {
        when(consultantRepository.count()).thenReturn(10L);
        when(consultantRepository.countByStatus("Available")).thenReturn(5L);
        when(consultantRepository.countByStatus("On Project")).thenReturn(3L);
        when(consultantRepository.countByStatus("Inactive")).thenReturn(2L);
        when(consultantRepository.countByCreatedAtAfter(any())).thenReturn(3L);
        when(consultantRepository.findAllTechnologies()).thenReturn(List.of("Java, Spring Boot", "React, Node.js"));

        ConsultantStatsDto stats = consultantService.getDashboardStats();

        assertThat(stats.getTotalConsultants()).isEqualTo(10L);
        assertThat(stats.getActiveConsultants()).isEqualTo(8L);
        assertThat(stats.getAvailableConsultants()).isEqualTo(5L);
        assertThat(stats.getOnProjectConsultants()).isEqualTo(3L);
        assertThat(stats.getInactiveConsultants()).isEqualTo(2L);
        assertThat(stats.getNewThisMonth()).isEqualTo(3L);
        assertThat(stats.getTechnologyDistribution()).containsKey("Java");
    }
}
