package com.nextimefood.msproduction.domain.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Event {
    @Id
    private UUID id;
    private UUID transactionId;
    private UUID orderId;
    private Order payload;
    private String source;
    private String status;
    private List<History> history;
    private LocalDateTime createdAt;
}
