package com.nextime.msproduction.infrastructure.adapter;

import com.nextime.msproduction.domain.ProductionStep;
import com.nextime.msproduction.domain.repository.ProductionStepRepository;
import com.nextime.msproduction.infrastructure.persistence.mapper.ProductionStepEntityMapper;
import com.nextime.msproduction.infrastructure.persistence.repository.ProductionStepJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class SqlProductionStepRepository implements ProductionStepRepository {

    private final ProductionStepJpaRepository jpaRepository;
    private final ProductionStepEntityMapper mapper;

    public SqlProductionStepRepository(
            ProductionStepJpaRepository jpaRepository,
            ProductionStepEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ProductionStep save(ProductionStep step) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(step)));
    }

    @Override
    public Optional<ProductionStep> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProductionStep> findByProductionOrderId(UUID productionOrderId) {
        return jpaRepository.findByProductionOrderId(productionOrderId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}


