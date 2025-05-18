package org.amir.ces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.amir.ces.model.Role;
import org.amir.ces.model.UserStatus;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDataDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String agency;
    private Role role;
    private UserStatus status;
} 