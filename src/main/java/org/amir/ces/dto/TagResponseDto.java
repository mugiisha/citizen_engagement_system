package org.amir.ces.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponseDto {
    private Long id;
    private String name;
}
