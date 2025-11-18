package com.nextime.msproduction.infrastructure.adapter;

import com.nextime.msproduction.domain.ProductionOrder;
import com.nextime.msproduction.domain.enums.ProductionStatus;
import com.nextime.msproduction.domain.repository.ProductionOrderRepository;
import com.nextime.msproduction.infrastructure.persistence.entity.ProductionOrderEntity;
import com.nextime.msproduction.infrastructure.persistence.mapper.ProductionOrderEntityMapper;
import com.nextime.msproduction.infrastructure.persistence.mapper.ProductionStepEntityMapper;
import com.nextime.msproduction.infrastructure.persistence.repository.ProductionOrderJpaRepository;
import com.nextime.msproduction.infrastructure.persistence.repository.ProductionStepJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class SqlProductionOrderRepository implements ProductionOrderRepository {

    private final ProductionOrderJpaRepository jpaRepository;
    private final ProductionStepJpaRepository stepJpaRepository;
    private final ProductionOrderEntityMapper mapper;
    private final ProductionStepEntityMapper stepMapper;

    public SqlProductionOrderRepository(
            ProductionOrderJpaRepository jpaRepository,
            ProductionStepJpaRepository stepJpaRepository,
            ProductionOrderEntityMapper mapper,
            ProductionStepEntityMapper stepMapper) {
        this.jpaRepository = jpaRepository;
        this.stepJpaRepository = stepJpaRepository;
        this.mapper = mapper;
        this.stepMapper = stepMapper;
    }

    @Override
    public ProductionOrder save(ProductionOrder order) {
        ProductionOrderEntity entity = mapper.toEntity(order);
        ProductionOrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ProductionOrder> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(entity -> {
                    ProductionOrder order = mapper.toDomain(entity);
                    loadSteps(order);
                    return order;
                });
    }

    @Override
    public Optional<ProductionOrder> findByOrderIdentifier(String orderIdentifier) {
        return jpaRepository.findByOrderIdentifier(orderIdentifier)
                .map(entity -> {
                    ProductionOrder order = mapper.toDomain(entity);
                    loadSteps(order);
                    return order;
                });
    }

    @Override
    public List<ProductionOrder> findAll() {
        return jpaRepository.findAll().stream()
                .map(entity -> {
                    ProductionOrder order = mapper.toDomain(entity);
                    loadSteps(order);
                    return order;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrder> findByStatus(ProductionStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(entity -> {
                    ProductionOrder order = mapper.toDomain(entity);
                    loadSteps(order);
                    return order;
                })
                .collect(Collectors.toList());
    }

    private void loadSteps(ProductionOrder order) {
        if (order != null && order.getId() != null) {
            order.setSteps(stepJpaRepository.findByProductionOrderId(order.getId()).stream()
                    .map(stepMapper::toDomain)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

