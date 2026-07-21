package com.countyassembly.caims.PurchaseOrder;

import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.supplier.Supplier;
import com.countyassembly.caims.supplier.SupplierService;
import com.countyassembly.caims.user.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MaterialService materialService;
    private final SupplierService supplierService;

    public PurchaseOrder create(
            PurchaseOrder order, Long materialId, Long supplierId, SystemUser requestedBy) {

        Material material = requireMaterial(materialId);
        Supplier supplier = requireSupplier(supplierId);

        order.setMaterial(material);
        order.setSupplier(supplier);
        order.setRequestedBy(requestedBy);
        order.setStatus(PurchaseOrderStatus.PENDING);

        return purchaseOrderRepository.save(order);
    }

    /**
     * Creates a Purchase Order that's already APPROVED. Used when
     * Procurement approves a Storekeeper's RestockRequest — picking the
     * supplier and approving happen in the same action, so there's no
     * separate PENDING step for this order (it's redundant: the same
     * officer would just be approving their own action a second time).
     */
    public PurchaseOrder createApproved(
            Integer quantity,
            Long materialId,
            Long supplierId,
            SystemUser requestedBy,
            SystemUser approvedBy) {

        Material material = requireMaterial(materialId);
        Supplier supplier = requireSupplier(supplierId);

        PurchaseOrder order = PurchaseOrder.builder()
                .quantity(quantity)
                .material(material)
                .supplier(supplier)
                .requestedBy(requestedBy)
                .approvedBy(approvedBy)
                .approvedAt(LocalDateTime.now())
                .status(PurchaseOrderStatus.APPROVED)
                .build();

        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder approve(Long id, SystemUser approver) {

        PurchaseOrder order = findById(id);

        if (order.getStatus() != PurchaseOrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending purchase order can be approved.");
        }

        order.setStatus(PurchaseOrderStatus.APPROVED);
        order.setApprovedBy(approver);
        order.setApprovedAt(LocalDateTime.now());

        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder reject(Long id, SystemUser approver) {

        PurchaseOrder order = findById(id);

        if (order.getStatus() != PurchaseOrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending purchase order can be rejected.");
        }

        order.setStatus(PurchaseOrderStatus.REJECTED);
        order.setApprovedBy(approver);
        order.setApprovedAt(LocalDateTime.now());

        return purchaseOrderRepository.save(order);
    }

    /**
     * Called by StockInService once a receipt has been recorded
     * against this order. Not exposed directly to a controller.
     */
    public PurchaseOrder markReceived(Long id) {

        PurchaseOrder order = findById(id);

        if (order.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only an approved purchase order can be received.");
        }

        order.setStatus(PurchaseOrderStatus.RECEIVED);

        return purchaseOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public PurchaseOrder findById(Long id) {

        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Purchase order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> findPending() {
        return purchaseOrderRepository.findByStatusOrderByCreatedAtDesc(PurchaseOrderStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> findApproved() {
        return purchaseOrderRepository.findByStatusOrderByCreatedAtDesc(PurchaseOrderStatus.APPROVED);
    }

    private Material requireMaterial(Long materialId) {

        if (materialId == null) {
            throw new IllegalArgumentException("A material must be selected.");
        }

        return materialService.findById(materialId);
    }

    private Supplier requireSupplier(Long supplierId) {

        if (supplierId == null) {
            throw new IllegalArgumentException("A supplier must be selected.");
        }

        return supplierService.findById(supplierId);
    }
}
