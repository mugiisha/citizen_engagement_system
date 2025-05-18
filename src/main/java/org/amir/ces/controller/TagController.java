package org.amir.ces.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.amir.ces.dto.ApiResponse;
import org.amir.ces.dto.CreateTagDto;
import org.amir.ces.model.Tag;
import org.amir.ces.repository.TagRepository;
import org.amir.ces.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<Tag>> createTag(@RequestBody @Valid CreateTagDto createTagDto) {
        Tag tag = tagService.createTag(createTagDto.getName());
        return  ResponseEntity.ok( ApiResponse.success(tag, "Tag created successfully"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Tag>>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(ApiResponse.success(tags, "Tags retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tag>> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        return ResponseEntity.ok(ApiResponse.success(tag, "Tag retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Tag>> updateTag(@PathVariable Long id, @RequestBody @Valid CreateTagDto createTagDto) {
        Tag tag = tagService.updateTag(id, createTagDto.getName());
        return ResponseEntity.ok(ApiResponse.success(tag, "Tag updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Tag deleted successfully"));
    }
}
