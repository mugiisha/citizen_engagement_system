package org.amir.ces.service;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.amir.ces.dto.AnalyticsResponseDto;
import org.amir.ces.dto.CreateTicketDto;
import org.amir.ces.dto.RespondToTicketDto;
import org.amir.ces.dto.TicketResponseDto;
import org.amir.ces.exception.BadRequestException;
import org.amir.ces.exception.ResourceNotFoundException;
import org.amir.ces.model.*;
import org.amir.ces.repository.TagRepository;
import org.amir.ces.repository.TicketRepository;
import org.amir.ces.repository.TicketSequenceRepository;
import org.amir.ces.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketSequenceRepository ticketSequenceRepository;
    private final AgencyService agencyService;
    private final EmailService emailService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;


    @Transactional
    public Ticket createTicket(CreateTicketDto createTicketDto){
        String referenceNumber = generateReferenceNumber();
        Agency agency = agencyService.getAgencyByName(createTicketDto.getAgency());

        Tag tag = tagRepository.findByName(createTicketDto.getTag());

        if(tag == null){
            throw new BadRequestException("Tag not found");
        }

        if(createTicketDto.getNotifyUser() == null){
            createTicketDto.setNotifyUser(false);
        }


        if(createTicketDto.getIssuerEmail() == null && createTicketDto.getNotifyUser()){
            throw new BadRequestException("Issuer email is required when you enabled notification");
        }

        Ticket ticket = Ticket
            .builder()
            .issuerName(createTicketDto.getIssuerName())
            .issuerEmail(createTicketDto.getIssuerEmail())
            .title(createTicketDto.getTitle())
            .description(createTicketDto.getDescription())
            .type(createTicketDto.getType())
            .tag(tag)
            .assignedAgency(agency)
            .notifyUser(createTicketDto.getNotifyUser())
            .referenceNumber(referenceNumber)
            .build();

            ticketRepository.save(ticket);

            if(ticket.getNotifyUser()){
                emailService.sendTicketCreatedEmail(ticket);
            }

            List<String> managerEmails = getManagerEmails(agency);

            emailService.sendIncomingTicket(ticket, managerEmails);

        return ticket;
    }


    @Transactional
    public Ticket respondToTicket(Long ticketId, RespondToTicketDto respondToTicketDto, String email) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ticket.setResponse(respondToTicketDto.getResponse());
        ticket.setStatus(respondToTicketDto.getStatus());
        ticket.setRespondedBy(user);
        ticket.setResolvedAt(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);

        if(savedTicket.getNotifyUser()){
            emailService.sendTicketUpdate(ticket, savedTicket.getIssuerEmail());
        }

        return savedTicket;
    }

    public List<TicketResponseDto> getDedicatedTickets(String email, String role) {
        Role userRole = Role.valueOf(role.split("_")[1]);

        log.info("user role: {}", userRole);

        if(Role.ADMIN.equals(userRole)){
            List<Ticket> tickets =ticketRepository.findAll();
            return tickets.stream()
                    .map(this::convertToResponseDto)
                    .toList();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Long> tagIds = user.getAgency().getTags().stream()
                .map(Tag::getId)
                .toList();

        List<Ticket> tickets = ticketRepository.findTicketsByTagIds(tagIds);
        return tickets.stream()
                .map(this::convertToResponseDto)
                .toList();
    }


    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public List<TicketResponseDto> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public TicketResponseDto getTicketByReferenceNumber(String referenceNumber) {
        Ticket ticket = ticketRepository.findByReferenceNumber(referenceNumber);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found");
        }
        return convertToResponseDto(ticket);
    }

    public AnalyticsResponseDto getTicketAnalytics(String email, String role) {
        Role userRole = Role.valueOf(role.split("_")[1]);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Ticket> tickets;

        if(Role.ADMIN.equals(userRole)){
            tickets = ticketRepository.findAll();
        }else{
            tickets = ticketRepository.findByAssignedAgencyId(user.getAgency().getId());
        }

        return formatAnalyticsResponse(tickets);

    }

    @Transactional
    protected String generateReferenceNumber() {
        // Get or create sequence
        TicketSequence sequence = ticketSequenceRepository.findById("TICKET_SEQ")
                .orElse(new TicketSequence());

        // Get current value and increment
        Long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + 1);

        // Save updated sequence
        ticketSequenceRepository.save(sequence);

        // Format as T001, T002, etc.
        return String.format("T%03d", currentValue);
    }

    public List<String> getManagerEmails(Agency agency){
        List<User> managers = userRepository.findByAgency(agency);
        return managers.stream()
                .filter(user -> Arrays.asList(Role.MANAGER, Role.ADMIN).contains(user.getRole()))
                .map(User::getEmail)
                .toList();
    }


    public AnalyticsResponseDto formatAnalyticsResponse(List<Ticket> tickets) {
        int totalTickets = tickets.size();
        int totalFeedbackTickets = (int) tickets.stream().filter(ticket -> ticket.getType() == TicketType.FEEDBACK).count();
        int totalComplaintTickets = (int) tickets.stream().filter(ticket -> ticket.getType() == TicketType.COMPLAINT).count();
        int totalAddressedTickets = (int) tickets.stream().filter(ticket -> ticket.getStatus() != TicketStatus.SUBMITTED).count();

        return AnalyticsResponseDto.builder()
                .totalTickets(totalTickets)
                .totalComplaintTickets(totalComplaintTickets)
                .totalFeedbackTickets(totalFeedbackTickets)
                .totalAddressedTickets(totalAddressedTickets)
                .build();
    }

    public TicketResponseDto convertToResponseDto(Ticket ticket) {
        return TicketResponseDto.builder()
                .id(ticket.getId())
                .referenceNumber(ticket.getReferenceNumber())
                .issuerName(ticket.getIssuerName())
                .issuerEmail(ticket.getIssuerEmail())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .type(ticket.getType())
                .tag(ticket.getTag().getName())
                .assignedAgency(ticket.getAssignedAgency().getName())
                .response(ticket.getResponse())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}
