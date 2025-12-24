package com.nextimefood.msproduction.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product {
    private Long id;
    private String name;
    private BigDecimal unitPrice;
}
