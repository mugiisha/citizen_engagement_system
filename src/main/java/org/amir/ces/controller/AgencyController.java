package org.amir.ces.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.amir.ces.dto.AgencyResponseDto;
import org.amir.ces.dto.ApiResponse;
import org.amir.ces.dto.CreateAgencyDto;
import org.amir.ces.model.Agency;
import org.amir.ces.service.AgencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agency")
@RequiredArgsConstructor
public class AgencyController {
    private final AgencyService agencyService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<Agency>> createAgency(@RequestBody @Valid CreateAgencyDto createAgencyDto) {
        Agency agency = agencyService.createAgency(createAgencyDto);
        return ResponseEntity.ok( ApiResponse.success(agency, "Agency created successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Agency>> updateAgency(@PathVariable Long id,@RequestBody CreateAgencyDto createAgencyDto) {
        Agency agency = agencyService.updateAgency(id, createAgencyDto);
        return ResponseEntity.ok(ApiResponse.success(agency, "Agency updated successfully"));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAgency(@PathVariable Long id) {
        agencyService.deleteAgency(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Agency deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Agency>> getAgencyById(@PathVariable Long id) {
        Agency agency = agencyService.getAgencyById(id);
        return ResponseEntity.ok(ApiResponse.success(agency, "Agency retrieved successfully"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<AgencyResponseDto>>> getAllAgencies() {
        List<AgencyResponseDto> agencies = agencyService.getAllAgencies();
        return ResponseEntity.ok(ApiResponse.success(agencies, "Agencies retrieved successfully"));
    }
}
