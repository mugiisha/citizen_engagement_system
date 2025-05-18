package org.amir.ces.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.amir.ces.exception.BadRequestException;
import org.amir.ces.model.Tag;
import org.amir.ces.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag createTag(String name) {
        Tag existingTag = tagRepository.findByName(name);
        if (existingTag != null) {
            throw new BadRequestException("Tag with this name already exists");
        }
        Tag tag = Tag.builder()
                .name(name.toLowerCase())
                .build();
        return tagRepository.save(tag);
    }

    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Tag not found"));
    }

    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    public Tag updateTag(Long id, String name) {
        Tag tag = getTagById(id);
        if (tagRepository.findByName(name) != null) {
            throw new BadRequestException("Tag with this name already exists");
        }
        tag.setName(name.toLowerCase());
        return tagRepository.save(tag);
    }

    public void deleteTag(Long id) {
        Tag tag = getTagById(id);
        tagRepository.delete(tag);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
