package com.countyassembly.caims.StockIn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInRepository extends JpaRepository<StockIn, Long> {

    List<StockIn> findAllByOrderByCreatedAtDesc();
}