package com.countyassembly.caims.stockout;

import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import com.countyassembly.caims.department.Department;
import com.countyassembly.caims.department.DepartmentService;
import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.user.SystemUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * StockOut Service
 * ============================================================
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StockOutService {

    private final StockOutRepository stockOutRepository;
    private final MaterialService materialService;
    private final DepartmentService departmentService;

    public StockOut issue(
            StockOut stockOut,
            Long materialId,
            Long departmentId,
            SystemUser issuedBy) {

        Material material = materialService.findById(materialId);
        Department department = departmentService.findById(departmentId);

        // decreaseStock() guards against taking stock negative — throws
        // before anything is saved, so a failed issue never gets
        // recorded halfway.
        materialService.decreaseStock(materialId, stockOut.getQuantity());

        stockOut.setMaterial(material);
        stockOut.setDepartment(department);
        stockOut.setIssuedBy(issuedBy);

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
