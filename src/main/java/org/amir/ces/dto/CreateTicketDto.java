package org.amir.ces.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.amir.ces.model.TicketType;

@Data
public class CreateTicketDto {
    private String issuerEmail;
    private String issuerName;
    @NotNull(message = "Title is required")
    private String title;
    @NotNull(message = "Description is required")
    private String description;
    @NotNull(message = "type is required")
    private TicketType type;
    @NotNull(message = "Agency id is required")
    private String agency;
    @NotNull(message = "Tag id is required")
    private String tag;
    private Boolean notifyUser;

}
