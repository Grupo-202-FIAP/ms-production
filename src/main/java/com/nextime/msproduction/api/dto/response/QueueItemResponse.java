package com.nextime.msproduction.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueItemResponse {
    private String productName;
    private Integer quantity;
    private String status;
}


