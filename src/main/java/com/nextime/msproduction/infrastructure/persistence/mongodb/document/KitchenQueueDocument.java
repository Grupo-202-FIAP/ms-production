package com.nextime.msproduction.infrastructure.persistence.mongodb.document;

import com.nextime.msproduction.domain.enums.ProductionStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "kitchen_queue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenQueueDocument {
    @Id
    private String id;

    private UUID productionOrderId;
    private String orderIdentifier;
    private ProductionStatus status;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QueueItemDocument> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueItemDocument {
        private String productName;
        private Integer quantity;
        private ProductionStatus status;
    }
}


