package org.amir.ces.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnalyticsResponseDto {
    private long totalTickets;
    private long totalFeedbackTickets;
    private long totalComplaintTickets;
    private long totalAddressedTickets;
}
