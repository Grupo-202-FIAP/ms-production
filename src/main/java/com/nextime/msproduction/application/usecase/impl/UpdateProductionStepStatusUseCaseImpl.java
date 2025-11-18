package com.nextime.msproduction.application.usecase.impl;

import com.nextime.msproduction.application.dto.ProductionStepResponse;
import com.nextime.msproduction.application.dto.UpdateProductionStepStatusRequest;
import com.nextime.msproduction.application.mapper.ProductionStepMapper;
import com.nextime.msproduction.application.usecase.UpdateProductionStepStatusUseCase;
import com.nextime.msproduction.domain.ProductionStep;
import com.nextime.msproduction.domain.enums.ProductionStatus;
import com.nextime.msproduction.domain.repository.ProductionStepRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductionStepStatusUseCaseImpl implements UpdateProductionStepStatusUseCase {

    private final ProductionStepRepository productionStepRepository;
    private final ProductionStepMapper productionStepMapper;

    public UpdateProductionStepStatusUseCaseImpl(
            ProductionStepRepository productionStepRepository,
            ProductionStepMapper productionStepMapper) {
        this.productionStepRepository = productionStepRepository;
        this.productionStepMapper = productionStepMapper;
    }

    @Override
    public ProductionStepResponse execute(UUID stepId, UpdateProductionStepStatusRequest request) {
        ProductionStep step = productionStepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Production step not found: " + stepId));

        ProductionStatus newStatus = ProductionStatus.valueOf(request.getStatus());
        step.updateStatus(newStatus);

        if (request.getNotes() != null) {
            step.setNotes(request.getNotes());
        }

        ProductionStep savedStep = productionStepRepository.save(step);
        return productionStepMapper.toResponse(savedStep);
    }
}


