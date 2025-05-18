package org.amir.ces.repository;

import org.amir.ces.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findByReferenceNumber(String referenceNumber);
}
