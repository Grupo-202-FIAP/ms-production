package com.nextime.msproduction.infrastructure.persistence.mongodb.mapper;

import com.nextime.msproduction.domain.KitchenQueue;
import com.nextime.msproduction.infrastructure.persistence.mongodb.document.KitchenQueueDocument;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class KitchenQueueDocumentMapper {

    public KitchenQueueDocument toDocument(KitchenQueue queue) {
        if (queue == null) {
            return null;
        }

        return KitchenQueueDocument.builder()
                .id(queue.getId())
                .productionOrderId(queue.getProductionOrderId())
                .orderIdentifier(queue.getOrderIdentifier())
                .status(queue.getStatus())
                .priority(queue.getPriority())
                .createdAt(queue.getCreatedAt())
                .updatedAt(queue.getUpdatedAt())
                .items(queue.getItems() != null
                        ? queue.getItems().stream()
                                .map(this::toQueueItemDocument)
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    public KitchenQueue toDomain(KitchenQueueDocument document) {
        if (document == null) {
            return null;
        }

        KitchenQueue queue = new KitchenQueue();
        queue.setId(document.getId());
        queue.setProductionOrderId(document.getProductionOrderId());
        queue.setOrderIdentifier(document.getOrderIdentifier());
        queue.setStatus(document.getStatus());
        queue.setPriority(document.getPriority());
        queue.setCreatedAt(document.getCreatedAt());
        queue.setUpdatedAt(document.getUpdatedAt());

        if (document.getItems() != null) {
            queue.setItems(document.getItems().stream()
                    .map(this::toQueueItem)
                    .collect(Collectors.toList()));
        }

        return queue;
    }

    private KitchenQueueDocument.QueueItemDocument toQueueItemDocument(KitchenQueue.QueueItem item) {
        return KitchenQueueDocument.QueueItemDocument.builder()
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .status(item.getStatus())
                .build();
    }

    private KitchenQueue.QueueItem toQueueItem(KitchenQueueDocument.QueueItemDocument document) {
        return new KitchenQueue.QueueItem(
                document.getProductName(),
                document.getQuantity(),
                document.getStatus()
        );
    }
}


