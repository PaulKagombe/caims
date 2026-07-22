package com.countyassembly.caims.stockout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOutRepository extends JpaRepository<StockOut, Long> {

    List<StockOut> findAllByOrderByCreatedAtDesc();
}
