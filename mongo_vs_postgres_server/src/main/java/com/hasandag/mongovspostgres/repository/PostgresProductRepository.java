package com.hasandag.mongovspostgres.repository;

import com.hasandag.mongovspostgres.model.PostgresProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresProductRepository extends JpaRepository<PostgresProduct, Long> {
} 