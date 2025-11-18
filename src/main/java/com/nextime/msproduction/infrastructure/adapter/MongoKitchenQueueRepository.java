package com.nextime.msproduction.infrastructure.adapter;

import com.nextime.msproduction.domain.KitchenQueue;
import com.nextime.msproduction.domain.enums.ProductionStatus;
import com.nextime.msproduction.domain.repository.KitchenQueueRepository;
import com.nextime.msproduction.infrastructure.persistence.mongodb.document.KitchenQueueDocument;
import com.nextime.msproduction.infrastructure.persistence.mongodb.mapper.KitchenQueueDocumentMapper;
import com.nextime.msproduction.infrastructure.persistence.repository.KitchenQueueMongoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MongoKitchenQueueRepository implements KitchenQueueRepository {

    private final KitchenQueueMongoRepository mongoRepository;
    private final KitchenQueueDocumentMapper mapper;

    public MongoKitchenQueueRepository(
            KitchenQueueMongoRepository mongoRepository,
            KitchenQueueDocumentMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public KitchenQueue save(KitchenQueue queue) {
        KitchenQueueDocument document = mapper.toDocument(queue);
        KitchenQueueDocument savedDocument = mongoRepository.save(document);
        return mapper.toDomain(savedDocument);
    }

    @Override
    public Optional<KitchenQueue> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<KitchenQueue> findByProductionOrderId(UUID productionOrderId) {
        return mongoRepository.findByProductionOrderId(productionOrderId)
                .map(mapper::toDomain);
    }

    @Override
    public List<KitchenQueue> findAll() {
        return mongoRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<KitchenQueue> findByStatus(ProductionStatus status) {
        return mongoRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}


