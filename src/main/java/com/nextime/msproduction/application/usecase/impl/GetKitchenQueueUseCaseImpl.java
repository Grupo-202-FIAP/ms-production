package com.nextime.msproduction.application.usecase.impl;

import com.nextime.msproduction.application.dto.KitchenQueueResponse;
import com.nextime.msproduction.application.mapper.KitchenQueueMapper;
import com.nextime.msproduction.application.usecase.GetKitchenQueueUseCase;
import com.nextime.msproduction.domain.repository.KitchenQueueRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GetKitchenQueueUseCaseImpl implements GetKitchenQueueUseCase {

    private final KitchenQueueRepository kitchenQueueRepository;
    private final KitchenQueueMapper kitchenQueueMapper;

    public GetKitchenQueueUseCaseImpl(
            KitchenQueueRepository kitchenQueueRepository,
            KitchenQueueMapper kitchenQueueMapper) {
        this.kitchenQueueRepository = kitchenQueueRepository;
        this.kitchenQueueMapper = kitchenQueueMapper;
    }

    @Override
    public List<KitchenQueueResponse> execute() {
        return kitchenQueueRepository.findAll().stream()
                .map(kitchenQueueMapper::toResponse)
                .collect(Collectors.toList());
    }
}


