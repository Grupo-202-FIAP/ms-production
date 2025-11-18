package com.nextime.msproduction.application.usecase;

import com.nextime.msproduction.application.dto.KitchenQueueResponse;
import java.util.List;

public interface GetKitchenQueueUseCase {
    List<KitchenQueueResponse> execute();
}


