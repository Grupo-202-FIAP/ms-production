package com.nextime.msproduction.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductionStepStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;
    private String notes;
}


