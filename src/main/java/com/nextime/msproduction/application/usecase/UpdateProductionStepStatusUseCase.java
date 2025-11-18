package com.nextime.msproduction.application.usecase;

import com.nextime.msproduction.application.dto.ProductionStepResponse;
import com.nextime.msproduction.application.dto.UpdateProductionStepStatusRequest;
import java.util.UUID;

public interface UpdateProductionStepStatusUseCase {
    ProductionStepResponse execute(UUID stepId, UpdateProductionStepStatusRequest request);
}


