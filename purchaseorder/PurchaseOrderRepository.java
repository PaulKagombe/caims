package com.countyassembly.caims.purchaseorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByStatusOrderByCreatedAtDesc(PurchaseOrderStatus status);

    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();
}
