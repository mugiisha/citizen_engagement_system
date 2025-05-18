package org.amir.ces.dto;

import lombok.Data;
import org.amir.ces.model.Role;

@Data
public class UpdateUserDto {
    private String email;
    private Role role;
    private long agencyId;
}
