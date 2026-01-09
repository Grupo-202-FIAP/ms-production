package com.nextimefood.msproduction.application.mapper;

import com.nextimefood.msproduction.domain.entity.Order;
import com.nextimefood.msproduction.infrastructure.persistence.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toDomain(OrderEntity orderEntity);

    OrderEntity toEntity(Order order);

}
