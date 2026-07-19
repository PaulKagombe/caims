package com.countyassembly.caims.StockRequest;

import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import com.countyassembly.caims.department.Department;
import com.countyassembly.caims.department.DepartmentService;
import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.user.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockRequestService {

    private final StockRequestRepository stockRequestRepository;
    private final MaterialService materialService;
    private final DepartmentService departmentService;

    public StockRequest create(
            StockRequest request, Long materialId, Long departmentId, SystemUser loggedBy) {

        Material material = requireMaterial(materialId);
        Department department = requireDepartment(departmentId);

        request.setMaterial(material);
        request.setDepartment(department);
        request.setLoggedBy(loggedBy);
        request.setStatus(StockRequestStatus.PENDING);

        return stockRequestRepository.save(request);
    }

    public StockRequest approve(Long id, SystemUser approver) {

        StockRequest request = findById(id);

        if (request.getStatus() != StockRequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending stock request can be approved.");
        }

        request.setStatus(StockRequestStatus.APPROVED);
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());

        return stockRequestRepository.save(request);
    }

    public StockRequest reject(Long id, SystemUser approver) {

        StockRequest request = findById(id);

        if (request.getStatus() != StockRequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending stock request can be rejected.");
        }

        request.setStatus(StockRequestStatus.REJECTED);
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());

        return stockRequestRepository.save(request);
    }

    /**
     * Called by StockOutService once an issue has been recorded
     * against this request. Not exposed directly to a controller.
     */
    public StockRequest markIssued(Long id) {

        StockRequest request = findById(id);

        if (request.getStatus() != StockRequestStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only an approved stock request can be issued.");
        }

        request.setStatus(StockRequestStatus.ISSUED);

        return stockRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public StockRequest findById(Long id) {

        return stockRequestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Stock request not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<StockRequest> findAll() {
        return stockRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<StockRequest> findPending() {
        return stockRequestRepository.findByStatusOrderByCreatedAtDesc(StockRequestStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<StockRequest> findApproved() {
        return stockRequestRepository.findByStatusOrderByCreatedAtDesc(StockRequestStatus.APPROVED);
    }

    private Material requireMaterial(Long materialId) {

        if (materialId == null) {
            throw new IllegalArgumentException("A material must be selected.");
        }

        return materialService.findById(materialId);
    }

    private Department requireDepartment(Long departmentId) {

        if (departmentId == null) {
            throw new IllegalArgumentException("A department must be selected.");
        }

        return departmentService.findById(departmentId);
    }
}
