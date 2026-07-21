package com.countyassembly.caims.restockrequest;

import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.PurchaseOrder.PurchaseOrder;
import com.countyassembly.caims.PurchaseOrder.PurchaseOrderService;
import com.countyassembly.caims.user.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestockRequestService {

    private final RestockRequestRepository restockRequestRepository;
    private final MaterialService materialService;
    private final PurchaseOrderService purchaseOrderService;

    public RestockRequest create(RestockRequest request, Long materialId, SystemUser requestedBy) {

        Material material = materialService.findById(materialId);

        request.setMaterial(material);
        request.setRequestedBy(requestedBy);
        request.setStatus(RestockRequestStatus.PENDING);

        return restockRequestRepository.save(request);
    }

    /**
     * Approves the request AND creates the resulting Purchase Order
     * (already APPROVED — see PurchaseOrderService.createApproved) in
     * one atomic step, since picking the supplier and approving are
     * the same action here.
     */
    public RestockRequest approve(Long id, Long supplierId, SystemUser reviewer) {

        RestockRequest request = findById(id);

        if (request.getStatus() != RestockRequestStatus.PENDING) {
            throw new IllegalStateException("Only a pending request can be approved.");
        }

        PurchaseOrder order = purchaseOrderService.createApproved(
                request.getQuantity(),
                request.getMaterial().getId(),
                supplierId,
                request.getRequestedBy(),
                reviewer);

        request.setStatus(RestockRequestStatus.APPROVED);
        request.setReviewedBy(reviewer);
        request.setReviewedAt(LocalDateTime.now());
        request.setPurchaseOrder(order);

        return restockRequestRepository.save(request);
    }

    public RestockRequest reject(Long id, SystemUser reviewer) {

        RestockRequest request = findById(id);

        if (request.getStatus() != RestockRequestStatus.PENDING) {
            throw new IllegalStateException("Only a pending request can be rejected.");
        }

        request.setStatus(RestockRequestStatus.REJECTED);
        request.setReviewedBy(reviewer);
        request.setReviewedAt(LocalDateTime.now());

        return restockRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public RestockRequest findById(Long id) {

        return restockRequestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restock request not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<RestockRequest> findAll() {
        return restockRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<RestockRequest> findPending() {
        return restockRequestRepository.findByStatusOrderByCreatedAtDesc(RestockRequestStatus.PENDING);
    }
}
