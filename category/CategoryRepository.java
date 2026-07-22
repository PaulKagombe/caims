package com.countyassembly.caims.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    /* ---------- Current methods ---------- */

    List<Category> findByActiveTrue();

    List<Category> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

    long countByActiveTrue();

    /* ---------- Pagination methods ---------- */

    Page<Category> findByActiveTrue(Pageable pageable);

    Page<Category> findByNameContainingIgnoreCaseAndActiveTrue(
            String keyword,
            Pageable pageable);

}
