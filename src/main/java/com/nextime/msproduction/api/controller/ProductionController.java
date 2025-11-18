package com.nextime.msproduction.api.controller;

import com.nextime.msproduction.api.dto.request.UpdateProductionStepStatusRequest;
import com.nextime.msproduction.api.dto.response.KitchenQueueResponse;
import com.nextime.msproduction.api.dto.response.ProductionOrderResponse;
import com.nextime.msproduction.api.dto.response.ProductionStepResponse;
import com.nextime.msproduction.api.dto.response.QueueItemResponse;
import com.nextime.msproduction.application.usecase.FinishProductionOrderUseCase;
import com.nextime.msproduction.application.usecase.GetKitchenQueueUseCase;
import com.nextime.msproduction.application.usecase.UpdateProductionStepStatusUseCase;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/production")
public class ProductionController {

    private final GetKitchenQueueUseCase getKitchenQueueUseCase;
    private final UpdateProductionStepStatusUseCase updateProductionStepStatusUseCase;
    private final FinishProductionOrderUseCase finishProductionOrderUseCase;

    public ProductionController(
            GetKitchenQueueUseCase getKitchenQueueUseCase,
            UpdateProductionStepStatusUseCase updateProductionStepStatusUseCase,
            FinishProductionOrderUseCase finishProductionOrderUseCase) {
        this.getKitchenQueueUseCase = getKitchenQueueUseCase;
        this.updateProductionStepStatusUseCase = updateProductionStepStatusUseCase;
        this.finishProductionOrderUseCase = finishProductionOrderUseCase;
    }

    @GetMapping("/queue")
    public ResponseEntity<List<KitchenQueueResponse>> getKitchenQueue() {
        List<com.nextime.msproduction.application.dto.KitchenQueueResponse> queue = getKitchenQueueUseCase.execute();
        List<KitchenQueueResponse> response = queue.stream()
                .map(this::toApiResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/step/{id}/status")
    public ResponseEntity<ProductionStepResponse> updateProductionStepStatus(
            @PathVariable UUID id,
            @RequestBody UpdateProductionStepStatusRequest request) {
        com.nextime.msproduction.application.dto.UpdateProductionStepStatusRequest appRequest =
                new com.nextime.msproduction.application.dto.UpdateProductionStepStatusRequest();
        appRequest.setStatus(request.getStatus());
        appRequest.setNotes(request.getNotes());

        com.nextime.msproduction.application.dto.ProductionStepResponse appResponse =
                updateProductionStepStatusUseCase.execute(id, appRequest);
        ProductionStepResponse response = toApiResponse(appResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<ProductionOrderResponse> finishProductionOrder(@PathVariable UUID id) {
        com.nextime.msproduction.application.dto.ProductionOrderResponse appResponse =
                finishProductionOrderUseCase.execute(id);
        ProductionOrderResponse response = toApiResponse(appResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private KitchenQueueResponse toApiResponse(com.nextime.msproduction.application.dto.KitchenQueueResponse appResponse) {
        return KitchenQueueResponse.builder()
                .id(appResponse.getId())
                .productionOrderId(appResponse.getProductionOrderId())
                .orderIdentifier(appResponse.getOrderIdentifier())
                .status(appResponse.getStatus())
                .priority(appResponse.getPriority())
                .createdAt(appResponse.getCreatedAt())
                .updatedAt(appResponse.getUpdatedAt())
                .items(appResponse.getItems() != null
                        ? appResponse.getItems().stream()
                                .map(item -> QueueItemResponse.builder()
                                        .productName(item.getProductName())
                                        .quantity(item.getQuantity())
                                        .status(item.getStatus())
                                        .build())
                                .toList()
                        : null)
                .build();
    }

    private ProductionStepResponse toApiResponse(com.nextime.msproduction.application.dto.ProductionStepResponse appResponse) {
        return ProductionStepResponse.builder()
                .id(appResponse.getId())
                .productionOrderId(appResponse.getProductionOrderId())
                .productName(appResponse.getProductName())
                .quantity(appResponse.getQuantity())
                .status(appResponse.getStatus())
                .notes(appResponse.getNotes())
                .startedAt(appResponse.getStartedAt())
                .completedAt(appResponse.getCompletedAt())
                .priority(appResponse.getPriority())
                .build();
    }

    private ProductionOrderResponse toApiResponse(com.nextime.msproduction.application.dto.ProductionOrderResponse appResponse) {
        return ProductionOrderResponse.builder()
                .id(appResponse.getId())
                .orderIdentifier(appResponse.getOrderIdentifier())
                .originalOrderId(appResponse.getOriginalOrderId())
                .totalPrice(appResponse.getTotalPrice())
                .status(appResponse.getStatus())
                .createdAt(appResponse.getCreatedAt())
                .updatedAt(appResponse.getUpdatedAt())
                .steps(appResponse.getSteps() != null
                        ? appResponse.getSteps().stream()
                                .map(this::toApiResponse)
                                .toList()
                        : null)
                .build();
    }
}

