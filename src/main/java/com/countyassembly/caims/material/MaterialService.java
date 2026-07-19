package com.countyassembly.caims.material;

import org.springframework.data.domain.Page;

import java.util.List;

public interface MaterialService {

    Material create(Material material, Long categoryId);

    Material update(Long id, Material incoming, Long categoryId);

    void delete(Long id);

    Material findById(Long id);

    List<Material> findAll();

    Page<Material> search(String keyword, Long categoryId, int page);

    long countActive();

    List<Material> findLowStock();

    long countLowStock();

    /**
     * Increase currentStock by quantity — called when a Stock In
     * (purchase order receipt) is recorded.
     */
    Material increaseStock(Long materialId, int quantity);

    /**
     * Decrease currentStock by quantity — called when a Stock Out
     * (issue) is recorded. Throws IllegalStateException if this
     * would take stock negative.
     */
    Material decreaseStock(Long materialId, int quantity);
}
