package com.countyassembly.caims.StockRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRequestRepository extends JpaRepository<StockRequest, Long> {

    List<StockRequest> findByStatusOrderByCreatedAtDesc(StockRequestStatus status);

    List<StockRequest> findAllByOrderByCreatedAtDesc();
}
