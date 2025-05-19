package org.amir.ces.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.amir.ces.model.*;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDto {
    private Long id;
    private String referenceNumber;
    private String issuerEmail;
    private String issuerName;
    private String title;
    private String description;
    private TicketType type;
    private String assignedAgency;
    private String tag;
    private String response;
    private String respondedBy;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
