package org.amir.ces.repository;

import org.amir.ces.model.Agency;
import org.amir.ces.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByAgency(Agency agency);
    boolean existsByEmail(String email);
}
