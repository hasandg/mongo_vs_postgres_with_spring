package com.example.mongovspostgres.repository;

import com.example.mongovspostgres.model.PostgresProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresProductRepository extends JpaRepository<PostgresProduct, Long> {
} 