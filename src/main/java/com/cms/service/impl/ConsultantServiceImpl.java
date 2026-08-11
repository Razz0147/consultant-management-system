package com.cms.service.impl;

import com.cms.dto.ConsultantDto;
import com.cms.dto.ConsultantStatsDto;
import com.cms.model.Consultant;
import com.cms.model.ActivityLog;
import com.cms.repository.ConsultantRepository;
import com.cms.repository.ActivityLogRepository;
import com.cms.service.ConsultantService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultantServiceImpl implements ConsultantService {

    private final ConsultantRepository consultantRepository;
    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ConsultantServiceImpl(ConsultantRepository consultantRepository, ActivityLogRepository activityLogRepository) {
        this.consultantRepository = consultantRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultantDto> getAllConsultants() {
        return consultantRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConsultantDto> getConsultants(String keyword, String status, Pageable pageable) {
        return consultantRepository.findByKeywordAndStatus(keyword, status, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultantDto getConsultantById(Long id) {
        Consultant consultant = consultantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultant not found with id: " + id));
        return convertToDto(consultant);
    }

    @Override
    public ConsultantDto createConsultant(ConsultantDto consultantDto) {
        if (consultantRepository.existsByEmail(consultantDto.getEmail())) {
            throw new IllegalArgumentException("Email '" + consultantDto.getEmail() + "' is already registered.");
        }
        Consultant consultant = convertToEntity(consultantDto);
        Consultant savedConsultant = consultantRepository.save(consultant);
        
        // Log dynamic notification activity
        activityLogRepository.save(new ActivityLog("Consultant '" + savedConsultant.getName() + "' was added as " + savedConsultant.getStatus() + "."));
        
        return convertToDto(savedConsultant);
    }

    @Override
    public ConsultantDto updateConsultant(Long id, ConsultantDto consultantDto) {
        Consultant existing = consultantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultant not found with id: " + id));

        if (consultantRepository.existsByEmailAndIdNot(consultantDto.getEmail(), id)) {
            throw new IllegalArgumentException("Email '" + consultantDto.getEmail() + "' is already used by another consultant.");
        }

        String oldName = existing.getName();
        String oldStatus = existing.getStatus();
        String newStatus = consultantDto.getStatus();

        existing.setName(consultantDto.getName());
        existing.setEmail(consultantDto.getEmail());
        existing.setPhone(consultantDto.getPhone());
        existing.setTechnology(consultantDto.getTechnology());
        existing.setExperience(consultantDto.getExperience());
        existing.setStatus(consultantDto.getStatus());

        Consultant updated = consultantRepository.save(existing);

        // Log dynamic activity
        String details = "Consultant '" + updated.getName() + "' profile was updated.";
        if (!oldStatus.equalsIgnoreCase(newStatus)) {
            details = "Consultant '" + updated.getName() + "' status changed from " + oldStatus + " to " + newStatus + ".";
        }
        activityLogRepository.save(new ActivityLog(details));

        return convertToDto(updated);
    }

    @Override
    public void deleteConsultant(Long id) {
        Consultant existing = consultantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultant not found with id: " + id));
        
        consultantRepository.delete(existing);
        
        // Log dynamic activity
        activityLogRepository.save(new ActivityLog("Consultant '" + existing.getName() + "' was deleted from system."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultantDto> searchConsultants(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllConsultants();
        }
        return consultantRepository.searchByNameOrTechnology(keyword.trim()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultantStatsDto getDashboardStats() {
        long total = consultantRepository.count();
        long available = consultantRepository.countByStatus("Available");
        long onProject = consultantRepository.countByStatus("On Project");
        long inactive = consultantRepository.countByStatus("Inactive");
        long active = available + onProject;
        
        // Count created in the last 30 days
        LocalDateTime startOfMonth = LocalDateTime.now().minusDays(30);
        long newThisMonth = consultantRepository.countByCreatedAtAfter(startOfMonth);

        List<String> rawTechs = consultantRepository.findAllTechnologies();
        Map<String, Long> techDist = new HashMap<>();
        for (String raw : rawTechs) {
            if (raw != null) {
                String[] parts = raw.split(",");
                for (String p : parts) {
                    String clean = p.trim();
                    if (!clean.isEmpty()) {
                        techDist.put(clean, techDist.getOrDefault(clean, 0L) + 1);
                    }
                }
            }
        }

        Map<String, Long> statusDist = new HashMap<>();
        statusDist.put("Available", available);
        statusDist.put("On Project", onProject);
        statusDist.put("Inactive", inactive);

        return ConsultantStatsDto.builder()
                .totalConsultants(total)
                .activeConsultants(active)
                .availableConsultants(available)
                .onProjectConsultants(onProject)
                .inactiveConsultants(inactive)
                .newThisMonth(newThisMonth)
                .totalTechnologies(techDist.size())
                .technologyDistribution(techDist)
                .statusDistribution(statusDist)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivities() {
        return activityLogRepository.findLatestLogs().stream().limit(10).toList();
    }

    @Override
    public void logActivity(String details) {
        activityLogRepository.save(new ActivityLog(details));
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportToExcel() {
        List<Consultant> consultants = consultantRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Consultants");

            // Header Font & Style
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Row Headers
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Email", "Phone", "Technology", "Experience (Years)", "Status", "Date Added"};

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Consultant c : consultants) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getName());
                row.createCell(2).setCellValue(c.getEmail());
                row.createCell(3).setCellValue(c.getPhone());
                row.createCell(4).setCellValue(c.getTechnology());
                row.createCell(5).setCellValue(c.getExperience());
                row.createCell(6).setCellValue(c.getStatus());
                row.createCell(7).setCellValue(c.getCreatedAt() != null ? c.getCreatedAt().format(formatter) : "N/A");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel report", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportToPdf() {
        List<Consultant> consultants = consultantRepository.findAll();
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Document Header
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("Consultant Directory Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            Paragraph subTitle = new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
            subTitle.setAlignment(Element.ALIGN_RIGHT);
            subTitle.setSpacingAfter(20);
            document.add(subTitle);

            // Table setup
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 3.5f, 4.5f, 3.0f, 4.0f, 2.0f, 2.0f});

            // Table Headers
            String[] headers = {"ID", "Name", "Email", "Phone", "Technology", "Exp (Yrs)", "Status"};
            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(30, 41, 59)); // Slate Navy
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Table Data
            com.lowagie.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            for (Consultant c : consultants) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(c.getId()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(c.getName(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(c.getEmail(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(c.getPhone(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(c.getTechnology(), dataFont)));
                
                PdfPCell expCell = new PdfPCell(new Phrase(String.valueOf(c.getExperience()), dataFont));
                expCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(expCell);

                PdfPCell statusCell = new PdfPCell(new Phrase(c.getStatus(), dataFont));
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(statusCell);
            }

            document.add(table);
            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to export PDF report", e);
        }
    }

    private ConsultantDto convertToDto(Consultant consultant) {
        return ConsultantDto.builder()
                .id(consultant.getId())
                .name(consultant.getName())
                .email(consultant.getEmail())
                .phone(consultant.getPhone())
                .technology(consultant.getTechnology())
                .experience(consultant.getExperience())
                .status(consultant.getStatus())
                .build();
    }

    private Consultant convertToEntity(ConsultantDto dto) {
        return Consultant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .technology(dto.getTechnology())
                .experience(dto.getExperience())
                .status(dto.getStatus() != null ? dto.getStatus() : "Available")
                .build();
    }
}
