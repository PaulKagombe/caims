package com.countyassembly.caims.category;

import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    Category save(Category category);

    Category update(Long id, Category category);

    void delete(Long id);

    Category findById(Long id);

    List<Category> findAll();

    List<Category> search(String keyword);

    Page<Category> findAll(int page);

    Page<Category> search(String keyword, int page);

    long countActive();

}
