package com.nextime.msproduction.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionStepResponse {
    private UUID id;
    private UUID productionOrderId;
    private String productName;
    private Integer quantity;
    private String status;
    private String notes;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer priority;
}


