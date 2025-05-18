package org.amir.ces.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String agency;
}
