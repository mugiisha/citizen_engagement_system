package org.amir.ces.repository;

import org.amir.ces.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    boolean existsByName(String name);
    Agency findByName(String name);
    // Custom query methods can be defined here if needed

}
