package com.cms.controller;

import com.cms.dto.ConsultantDto;
import com.cms.dto.ConsultantStatsDto;
import com.cms.service.ConsultantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultants")
public class ConsultantRestController {

    private final ConsultantService consultantService;

    @Autowired
    public ConsultantRestController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping
    public ResponseEntity<List<ConsultantDto>> getAllConsultants(
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(consultantService.searchConsultants(keyword));
        }
        return ResponseEntity.ok(consultantService.getAllConsultants());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ConsultantDto>> getPaginatedConsultants(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(consultantService.getConsultants(keyword, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultantDto> getConsultantById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(consultantService.getConsultantById(id));
    }

    @PostMapping
    public ResponseEntity<ConsultantDto> createConsultant(@Valid @RequestBody ConsultantDto consultantDto) {
        ConsultantDto created = consultantService.createConsultant(consultantDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultantDto> updateConsultant(
            @PathVariable("id") Long id, 
            @Valid @RequestBody ConsultantDto consultantDto) {
        ConsultantDto updated = consultantService.updateConsultant(id, consultantDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultant(@PathVariable("id") Long id) {
        consultantService.deleteConsultant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<ConsultantStatsDto> getDashboardStats() {
        return ResponseEntity.ok(consultantService.getDashboardStats());
    }
}
