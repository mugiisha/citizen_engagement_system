package org.amir.ces.dto;

import lombok.Data;
import org.amir.ces.model.TicketStatus;

@Data
public class RespondToTicketDto {
    TicketStatus status;
    String response;
}
