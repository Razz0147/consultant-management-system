package com.cms.controller;

import com.cms.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;

@Controller
@RequestMapping("/consultants/export")
public class ExportController {

    private final ConsultantService consultantService;

    @Autowired
    public ExportController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping("/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() {
        ByteArrayInputStream stream = consultantService.exportToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=consultants_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> exportToPdf() {
        ByteArrayInputStream stream = consultantService.exportToPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=consultants_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(stream));
    }
}
