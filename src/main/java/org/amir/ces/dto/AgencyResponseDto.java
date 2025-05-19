package org.amir.ces.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgencyResponseDto {
    private Long id;
    private String name;
    private String description;
    private String createdAt;

}
