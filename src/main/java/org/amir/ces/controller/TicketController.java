package org.amir.ces.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.amir.ces.dto.*;
import org.amir.ces.model.Ticket;
import org.amir.ces.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {
    private final TicketService ticketService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Ticket>> createTicket(@RequestBody @Valid CreateTicketDto createTicketDto) {
        Ticket ticket = ticketService.createTicket(createTicketDto);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket created successfully"));
    }


    @GetMapping("/{referenceNumber}")
    public ResponseEntity<ApiResponse<TicketResponseDto>> getTicket(@PathVariable String referenceNumber) {
        TicketResponseDto ticket = ticketService.getTicketByReferenceNumber(referenceNumber);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket retrieved successfully"));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}/respond")
    public ResponseEntity<ApiResponse<Ticket>> respondToTicket(
            @PathVariable Long id,
            @RequestBody @Valid RespondToTicketDto respondToTicketDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        Ticket ticket = ticketService.respondToTicket(id, respondToTicketDto, email);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket responded successfully"));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<TicketResponseDto>>> getDedicatedTickets(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        // Retrieve roles from the user's authorities
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("UNKNOWN");

        List<TicketResponseDto> tickets = ticketService.getDedicatedTickets(email, role);
        return ResponseEntity.ok(ApiResponse.success(tickets, "Tickets retrieved successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<AnalyticsResponseDto>> getTicketAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        // Retrieve roles from the user's authorities
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("UNKNOWN");

        AnalyticsResponseDto analyticsResponseDto = ticketService.getTicketAnalytics(email, role);
        return ResponseEntity.ok(ApiResponse.success(analyticsResponseDto, "Ticket analytics retrieved successfully"));
    }
}
