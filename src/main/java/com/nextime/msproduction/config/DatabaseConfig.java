package com.nextime.msproduction.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.nextime.msproduction.infrastructure.persistence.repository")
@EnableMongoRepositories(basePackages = "com.nextime.msproduction.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.nextime.msproduction.infrastructure.persistence.entity")
public class DatabaseConfig {
}


