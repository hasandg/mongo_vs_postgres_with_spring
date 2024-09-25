package com.example.mongovspostgres.service;

import com.example.mongovspostgres.model.MongoProduct;
import com.example.mongovspostgres.model.PostgresProduct;
import com.example.mongovspostgres.repository.MongoProductRepository;
import com.example.mongovspostgres.repository.PostgresProductRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class PerformanceTestService {
    private final MongoProductRepository mongoRepository;
    private final PostgresProductRepository postgresRepository;
    private final MeterRegistry meterRegistry;
    private final Random random = new Random();

    public PerformanceTestService(MongoProductRepository mongoRepository,
                                PostgresProductRepository postgresRepository,
                                MeterRegistry meterRegistry) {
        this.mongoRepository = mongoRepository;
        this.postgresRepository = postgresRepository;
        this.meterRegistry = meterRegistry;
    }

    public Map<String, Long> generateTestData(int count) {
        List<MongoProduct> mongoProducts = new ArrayList<>();
        List<PostgresProduct> postgresProducts = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            MongoProduct mongoProduct = new MongoProduct();
            mongoProduct.setName("Product " + i);
            mongoProduct.setDescription("Description for product " + i);
            mongoProduct.setPrice(random.nextDouble() * 1000);
            mongoProduct.setStock(random.nextInt(1000));
            mongoProduct.setCategory("Category " + (i % 10));
            mongoProduct.setTags(new String[]{"tag" + (i % 5), "tag" + ((i + 1) % 5)});
            mongoProducts.add(mongoProduct);

            PostgresProduct postgresProduct = new PostgresProduct();
            postgresProduct.setName("Product " + i);
            postgresProduct.setDescription("Description for product " + i);
            postgresProduct.setPrice(random.nextDouble() * 1000);
            postgresProduct.setStock(random.nextInt(1000));
            postgresProduct.setCategory("Category " + (i % 10));
            postgresProduct.setTags(new String[]{"tag" + (i % 5), "tag" + ((i + 1) % 5)});
            postgresProducts.add(postgresProduct);
        }

        // Save to MongoDB
        long mongoStart = System.currentTimeMillis();
        mongoRepository.saveAll(mongoProducts);
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;
        meterRegistry.timer("db.operation", "database", "mongodb", "operation", "insert")
                .record(java.time.Duration.ofMillis(mongoTime));
        System.out.println("MongoDB Insert Time: " + mongoTime + "ms");

        // Save to PostgreSQL
        long postgresStart = System.currentTimeMillis();
        postgresRepository.saveAll(postgresProducts);
        long postgresEnd = System.currentTimeMillis();
        long postgresTime = postgresEnd - postgresStart;
        meterRegistry.timer("db.operation", "database", "postgres", "operation", "insert")
                .record(java.time.Duration.ofMillis(postgresTime));
        System.out.println("PostgreSQL Insert Time: " + postgresTime + "ms");

        Map<String, Long> results = new HashMap<>();
        results.put("mongodb", mongoTime);
        results.put("postgres", postgresTime);
        return results;
    }

    public Map<String, Long> testReadPerformance(int count) {
        // Test MongoDB read
        long mongoStart = System.currentTimeMillis();
        List<MongoProduct> mongoProducts = mongoRepository.findAll();
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;
        meterRegistry.timer("db.operation", "database", "mongodb", "operation", "read")
                .record(java.time.Duration.ofMillis(mongoTime));
        System.out.println("MongoDB Read Time: " + mongoTime + "ms");

        // Test PostgreSQL read
        long postgresStart = System.currentTimeMillis();
        List<PostgresProduct> postgresProducts = postgresRepository.findAll();
        long postgresEnd = System.currentTimeMillis();
        long postgresTime = postgresEnd - postgresStart;
        meterRegistry.timer("db.operation", "database", "postgres", "operation", "read")
                .record(java.time.Duration.ofMillis(postgresTime));
        System.out.println("PostgreSQL Read Time: " + postgresTime + "ms");

        Map<String, Long> results = new HashMap<>();
        results.put("mongodb", mongoTime);
        results.put("postgres", postgresTime);
        return results;
    }

    public Map<String, Long> testUpdatePerformance(int count) {
        List<MongoProduct> mongoProducts = mongoRepository.findAll();
        List<PostgresProduct> postgresProducts = postgresRepository.findAll();

        // Test MongoDB update
        long mongoStart = System.currentTimeMillis();
        for (int i = 0; i < Math.min(count, mongoProducts.size()); i++) {
            MongoProduct product = mongoProducts.get(i);
            product.setPrice(product.getPrice() * 1.1);
            mongoRepository.save(product);
        }
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;
        meterRegistry.timer("db.operation", "database", "mongodb", "operation", "update")
                .record(java.time.Duration.ofMillis(mongoTime));
        System.out.println("MongoDB Update Time: " + mongoTime + "ms");

        // Test PostgreSQL update
        long postgresStart = System.currentTimeMillis();
        for (int i = 0; i < Math.min(count, postgresProducts.size()); i++) {
            PostgresProduct product = postgresProducts.get(i);
            product.setPrice(product.getPrice() * 1.1);
            postgresRepository.save(product);
        }
        long postgresEnd = System.currentTimeMillis();
        long postgresTime = postgresEnd - postgresStart;
        meterRegistry.timer("db.operation", "database", "postgres", "operation", "update")
                .record(java.time.Duration.ofMillis(postgresTime));
        System.out.println("PostgreSQL Update Time: " + postgresTime + "ms");

        Map<String, Long> results = new HashMap<>();
        results.put("mongodb", mongoTime);
        results.put("postgres", postgresTime);
        return results;
    }

    public Map<String, Long> testDeletePerformance(int count) {
        List<MongoProduct> mongoProducts = mongoRepository.findAll();
        List<PostgresProduct> postgresProducts = postgresRepository.findAll();

        // Test MongoDB delete
        long mongoStart = System.currentTimeMillis();
        for (int i = 0; i < Math.min(count, mongoProducts.size()); i++) {
            mongoRepository.delete(mongoProducts.get(i));
        }
        long mongoEnd = System.currentTimeMillis();
        long mongoTime = mongoEnd - mongoStart;
        meterRegistry.timer("db.operation", "database", "mongodb", "operation", "delete")
                .record(java.time.Duration.ofMillis(mongoTime));
        System.out.println("MongoDB Delete Time: " + mongoTime + "ms");

        // Test PostgreSQL delete
        long postgresStart = System.currentTimeMillis();
        for (int i = 0; i < Math.min(count, postgresProducts.size()); i++) {
            postgresRepository.delete(postgresProducts.get(i));
        }
        long postgresEnd = System.currentTimeMillis();
        long postgresTime = postgresEnd - postgresStart;
        meterRegistry.timer("db.operation", "database", "postgres", "operation", "delete")
                .record(java.time.Duration.ofMillis(postgresTime));
        System.out.println("PostgreSQL Delete Time: " + postgresTime + "ms");

        Map<String, Long> results = new HashMap<>();
        results.put("mongodb", mongoTime);
        results.put("postgres", postgresTime);
        return results;
    }
} 