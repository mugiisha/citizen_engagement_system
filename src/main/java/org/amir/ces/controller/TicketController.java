package org.amir.ces.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.amir.ces.dto.ApiResponse;
import org.amir.ces.dto.CreateTicketDto;
import org.amir.ces.dto.RespondToTicketDto;
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
    public ResponseEntity<ApiResponse<Ticket>> getTicket(@PathVariable String referenceNumber) {
        Ticket ticket = ticketService.getTicketByReferenceNumber(referenceNumber);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<Ticket>>> getAllTickets() {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getAllTickets(), "Tickets retrieved successfully"));
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

}
