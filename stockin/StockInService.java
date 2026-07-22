package com.countyassembly.caims.stockin;

import com.countyassembly.caims.common.exception.ResourceNotFoundException;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.purchaseorder.PurchaseOrder;
import com.countyassembly.caims.purchaseorder.PurchaseOrderService;
import com.countyassembly.caims.user.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * StockIn Service
 * ============================================================
 *
 * receive() is the one operation this module exposes, and it's
 * fully transactional: creating the StockIn audit record,
 * increasing Material.currentStock, and marking the
 * PurchaseOrder RECEIVED all happen together or not at all.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StockInService {

    private final StockInRepository stockInRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final MaterialService materialService;

    public StockIn receive(Long purchaseOrderId, String notes, SystemUser receivedBy) {

        PurchaseOrder order = purchaseOrderService.findById(purchaseOrderId);

        // markReceived() itself guards that the order is APPROVED,
        // throwing IllegalStateException otherwise — checked first so
        // we never touch stock levels for an order that can't be received.
        purchaseOrderService.markReceived(purchaseOrderId);

        materialService.increaseStock(order.getMaterial().getId(), order.getQuantity());

        StockIn stockIn = StockIn.builder()
                .purchaseOrder(order)
                .quantityReceived(order.getQuantity())
                .notes(notes)
                .receivedBy(receivedBy)
                .build();

        return stockInRepository.save(stockIn);
    }

    @Transactional(readOnly = true)
    public StockIn findById(Long id) {

        return stockInRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock-in record not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<StockIn> findAll() {
        return stockInRepository.findAllByOrderByCreatedAtDesc();
    }
}
