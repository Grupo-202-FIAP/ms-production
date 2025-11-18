package com.nextime.msproduction.application.mapper;

import com.nextime.msproduction.application.dto.KitchenQueueResponse;
import com.nextime.msproduction.application.dto.QueueItemResponse;
import com.nextime.msproduction.domain.KitchenQueue;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class KitchenQueueMapper {

    public KitchenQueueResponse toResponse(KitchenQueue queue) {
        if (queue == null) {
            return null;
        }

        return KitchenQueueResponse.builder()
                .id(queue.getId())
                .productionOrderId(queue.getProductionOrderId())
                .orderIdentifier(queue.getOrderIdentifier())
                .status(queue.getStatus() != null ? queue.getStatus().getStatus() : null)
                .priority(queue.getPriority())
                .createdAt(queue.getCreatedAt())
                .updatedAt(queue.getUpdatedAt())
                .items(queue.getItems() != null
                        ? queue.getItems().stream()
                                .map(this::toQueueItemResponse)
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    private QueueItemResponse toQueueItemResponse(KitchenQueue.QueueItem item) {
        return QueueItemResponse.builder()
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .status(item.getStatus() != null ? item.getStatus().getStatus() : null)
                .build();
    }
}


