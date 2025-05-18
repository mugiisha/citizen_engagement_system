package org.amir.ces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.amir.ces.model.Role;

@Data
public class CreateUserDto {

    @NotNull(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @Email(message = "Email should be valid")
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @NotNull(message = "Role is required")
    @NotBlank(message = "Role cannot be blank")
    private Role role;
    @NotNull(message = "Agency is required")
    @NotBlank(message = "Agency cannot be blank")
    private Integer agencyId;
}
