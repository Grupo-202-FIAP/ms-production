package com.nextimefood.msproduction.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private String source;
    private String status;
    private String message;
    private LocalDateTime createdAt;
}
