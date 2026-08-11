package com.cms.service;

import com.cms.dto.ConsultantDto;
import com.cms.dto.ConsultantStatsDto;
import com.cms.model.Consultant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ConsultantService {
    List<ConsultantDto> getAllConsultants();
    
    Page<ConsultantDto> getConsultants(String keyword, String status, Pageable pageable);
    
    ConsultantDto getConsultantById(Long id);
    
    ConsultantDto createConsultant(ConsultantDto consultantDto);
    
    ConsultantDto updateConsultant(Long id, ConsultantDto consultantDto);
    
    void deleteConsultant(Long id);
    
    List<ConsultantDto> searchConsultants(String keyword);
    
    ConsultantStatsDto getDashboardStats();
    
    ByteArrayInputStream exportToExcel();
    
    ByteArrayInputStream exportToPdf();

    List<com.cms.model.ActivityLog> getRecentActivities();
    
    void logActivity(String details);
}
