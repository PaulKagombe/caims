package com.countyassembly.caims.category;

import com.countyassembly.caims.common.entity.DuplicateResourceException;
import com.countyassembly.caims.common.entity.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * Category Service Implementation
 * ============================================================
 *
 * Handles all business logic related to categories.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final int PAGE_SIZE = 10;

    private final CategoryRepository categoryRepository;

    /**
     * Create a new category.
     */
    @Override
    public Category save(Category category) {

        String name = category.getName().trim();

        Optional<Category> existing = categoryRepository.findByName(name);

        if (existing.isPresent()) {

            Category oldCategory = existing.get();

            // Reactivate previously deleted category
            if (Boolean.FALSE.equals(oldCategory.getActive())) {

                oldCategory.setActive(true);
                oldCategory.setDescription(category.getDescription());

                return categoryRepository.save(oldCategory);
            }

            throw new DuplicateResourceException(
                    "An active category with this name already exists.");
        }

        category.setName(name);
        category.setActive(true);

        return categoryRepository.save(category);
    }

    /**
     * Update category.
     */
    @Override
    public Category update(Long id, Category category) {

        Category existing = findById(id);

        String name = category.getName().trim();

        if (!existing.getName().equalsIgnoreCase(name)
                && categoryRepository.existsByName(name)) {

            throw new DuplicateResourceException("Category already exists.");
        }

        existing.setName(name);
        existing.setDescription(category.getDescription());
        existing.setActive(category.getActive());

        return categoryRepository.save(existing);
    }

    /**
     * Soft delete category.
     */
    @Override
    public void delete(Long id) {

        Category category = findById(id);

        category.setActive(false);

        categoryRepository.save(category);
    }

    /**
     * Find category by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id));
    }

    /**
     * Return all active categories.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {

        return categoryRepository.findByActiveTrue();
    }

    /**
     * Search active categories.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Category> search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return categoryRepository.findByActiveTrue();
        }

        return categoryRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(keyword.trim());
    }

    /**
     * Return paginated active categories.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Category> findAll(int page) {

        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE);

        return categoryRepository.findByActiveTrue(pageable);
    }

    /**
     * Search with pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Category> search(String keyword, int page) {

        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE);

        if (keyword == null || keyword.isBlank()) {
            return categoryRepository.findByActiveTrue(pageable);
        }

        return categoryRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        keyword.trim(),
                        pageable);
    }

    /**
     * Count of active categories, used by the dashboard KPI card.
     */
    @Override
    @Transactional(readOnly = true)
    public long countActive() {

        return categoryRepository.countByActiveTrue();
    }
}
