package com.example.mongovspostgres.repository;

import com.example.mongovspostgres.model.MongoProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoProductRepository extends MongoRepository<MongoProduct, String> {
} 