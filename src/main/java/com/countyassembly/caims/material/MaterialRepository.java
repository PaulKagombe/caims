package com.countyassembly.caims.material;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByBarcode(String barcode);

    List<Material> findByActiveTrue();

    long countByActiveTrue();

    Page<Material> findByActiveTrue(Pageable pageable);

    Page<Material> findByNameContainingIgnoreCaseAndActiveTrue(
            String keyword,
            Pageable pageable);

    Page<Material> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Page<Material> findByCategoryIdAndNameContainingIgnoreCaseAndActiveTrue(
            Long categoryId,
            String keyword,
            Pageable pageable);

    /**
     * Active materials whose stock has fallen to or below their
     * reorder level — used for low-stock alerts on the dashboard.
     */
    @Query("SELECT m FROM Material m WHERE m.active = true AND m.currentStock <= m.reorderLevel")
    List<Material> findLowStock();

    @Query("SELECT COUNT(m) FROM Material m WHERE m.active = true AND m.currentStock <= m.reorderLevel")
    long countLowStock();
}