package org.amir.ces.repository;

import org.amir.ces.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findByReferenceNumber(String referenceNumber);

    @Query("SELECT t FROM Ticket t WHERE t.tag.id IN :tagIds")
    List<Ticket> findTicketsByTagIds(@Param("tagIds") List<Long> tagIds);
}
