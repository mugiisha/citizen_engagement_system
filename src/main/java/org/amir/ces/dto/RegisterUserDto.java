package org.amir.ces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.amir.ces.model.Role;

@Data
public class RegisterUserDto {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Role must not be null")
    private Role role;
    @NotNull(message = "Agency id must not be null")
    private String agency;
}
