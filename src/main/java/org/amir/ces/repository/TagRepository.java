package org.amir.ces.repository;

import org.amir.ces.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);
    List<Tag> findByNameIn(List<String> names);

}
