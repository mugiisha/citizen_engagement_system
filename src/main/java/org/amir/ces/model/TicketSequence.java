package org.amir.ces.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket_sequence")
@Data
@NoArgsConstructor
public class TicketSequence {
    @Id
    private String id = "TICKET_SEQ"; // Only one row will exist

    private Long nextValue = 1L;
}
