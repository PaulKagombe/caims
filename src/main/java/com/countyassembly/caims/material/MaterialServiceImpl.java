package com.countyassembly.caims.material;

import com.countyassembly.caims.category.Category;
import com.countyassembly.caims.category.CategoryService;
import com.countyassembly.caims.common.entity.DuplicateResourceException;
import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * Material Service Implementation
 * ============================================================
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private static final int PAGE_SIZE = 10;

    private final MaterialRepository materialRepository;
    private final CategoryService categoryService;

    @Override
    public Material create(Material material, Long categoryId) {

        String code = material.getCode().trim();

        if (materialRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException(
                    "A material with code '" + code + "' already exists.");
        }

        Category category = resolveCategory(categoryId);

        if (material.getBarcode() != null && !material.getBarcode().isBlank()
                && materialRepository.existsByBarcode(material.getBarcode().trim())) {
            throw new DuplicateResourceException(
                    "A material with barcode '" + material.getBarcode() + "' already exists.");
        }

        material.setCode(code);
        material.setCategory(category);
        material.setBarcode(normalizeBarcode(material.getBarcode()));

        if (material.getActive() == null) {
            material.setActive(true);
        }

        return materialRepository.save(material);
    }

    @Override
    public Material update(Long id, Material incoming, Long categoryId) {

        Material existing = findById(id);

        String code = incoming.getCode().trim();

        if (!existing.getCode().equalsIgnoreCase(code)
                && materialRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException(
                    "A material with code '" + code + "' already exists.");
        }

        Category category = resolveCategory(categoryId);

        String barcode = normalizeBarcode(incoming.getBarcode());

        if (barcode != null
                && !barcode.equalsIgnoreCase(existing.getBarcode())
                && materialRepository.existsByBarcode(barcode)) {
            throw new DuplicateResourceException(
                    "A material with barcode '" + barcode + "' already exists.");
        }

        existing.setCode(code);
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setUnit(incoming.getUnit());
        existing.setBarcode(barcode);
        existing.setUnitPrice(incoming.getUnitPrice());
        existing.setCurrentStock(incoming.getCurrentStock());
        existing.setReorderLevel(incoming.getReorderLevel());
        existing.setActive(incoming.getActive());
        existing.setCategory(category);

        return materialRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        Material material = findById(id);

        material.setActive(false);

        materialRepository.save(material);
    }

    @Override
    @Transactional(readOnly = true)
    public Material findById(Long id) {

        return materialRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Material not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Material> findAll() {
        return materialRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Material> search(String keyword, Long categoryId, int page) {

        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE);

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = categoryId != null;

        if (hasKeyword && hasCategory) {
            return materialRepository.findByCategoryIdAndNameContainingIgnoreCaseAndActiveTrue(
                    categoryId, keyword.trim(), pageable);
        }

        if (hasCategory) {
            return materialRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        }

        if (hasKeyword) {
            return materialRepository.findByNameContainingIgnoreCaseAndActiveTrue(
                    keyword.trim(), pageable);
        }

        return materialRepository.findByActiveTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        return materialRepository.countByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Material> findLowStock() {
        return materialRepository.findLowStock();
    }

    @Override
    @Transactional(readOnly = true)
    public long countLowStock() {
        return materialRepository.countLowStock();
    }

    @Override
    public Material increaseStock(Long materialId, int quantity) {

        Material material = findById(materialId);

        material.setCurrentStock(material.getCurrentStock() + quantity);

        return materialRepository.save(material);
    }

    @Override
    public Material decreaseStock(Long materialId, int quantity) {

        Material material = findById(materialId);

        int remaining = material.getCurrentStock() - quantity;

        if (remaining < 0) {
            throw new IllegalStateException(
                    "Insufficient stock for '" + material.getName() + "': only "
                            + material.getCurrentStock() + " " + material.getUnit() + " available.");
        }

        material.setCurrentStock(remaining);

        return materialRepository.save(material);
    }

    private Category resolveCategory(Long categoryId) {

        if (categoryId == null) {
            throw new IllegalArgumentException("A category must be selected.");
        }

        return categoryService.findById(categoryId);
    }

    private String normalizeBarcode(String barcode) {
        return (barcode == null || barcode.isBlank()) ? null : barcode.trim();
    }
}
