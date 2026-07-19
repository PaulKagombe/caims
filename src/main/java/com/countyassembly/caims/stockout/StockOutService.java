package com.countyassembly.caims.stockout;

import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.StockRequest.StockRequest;
import com.countyassembly.caims.StockRequest.StockRequestService;
import com.countyassembly.caims.user.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockOutService {

    private final StockOutRepository stockOutRepository;
    private final StockRequestService stockRequestService;
    private final MaterialService materialService;

    public StockOut issue(Long stockRequestId, String notes, SystemUser issuedBy) {

        StockRequest request = stockRequestService.findById(stockRequestId);

        // markIssued() guards that the request is APPROVED, and
        // decreaseStock() guards against taking stock negative —
        // both throw IllegalStateException, checked before saving
        // the audit record so nothing is recorded halfway.
        stockRequestService.markIssued(stockRequestId);

        materialService.decreaseStock(request.getMaterial().getId(), request.getQuantity());

        StockOut stockOut = StockOut.builder()
                .stockRequest(request)
                .quantityIssued(request.getQuantity())
                .notes(notes)
                .issuedBy(issuedBy)
                .build();

        return stockOutRepository.save(stockOut);
    }

    @Transactional(readOnly = true)
    public StockOut findById(Long id) {

        return stockOutRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock-out record not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<StockOut> findAll() {
        return stockOutRepository.findAllByOrderByCreatedAtDesc();
    }
}