package org.amir.ces.repository;

import org.amir.ces.model.TicketSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketSequenceRepository extends JpaRepository<TicketSequence, String> {
}
