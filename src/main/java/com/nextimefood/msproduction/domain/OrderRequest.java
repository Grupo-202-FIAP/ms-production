package com.nextimefood.msproduction.domain;

import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private List<OrderItem> orderItems;
}
