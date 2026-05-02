package com.assignment3.project.services;

import com.assignment3.project.dto.requests.CategoryCreateRequest;
import com.assignment3.project.dto.requests.CategoryUpdateRequest;
import com.assignment3.project.dto.responses.CategoryResponse;
import com.assignment3.project.entities.Category;
import com.assignment3.project.mappers.CategoryMapper;
import com.assignment3.project.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {
        log.info("CategoryService.getAllCategories called");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        log.info("CategoryService.getCategoryById called id={}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        return categoryMapper.toDto(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        log.info("CategoryService.createCategory called name={}", request.getName());
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        log.info("CategoryService.updateCategory called id={} name={}", id, request.getName());
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("CategoryService.deleteCategory called id={}", id);
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }
        categoryRepository.deleteById(id);
        log.info("Category with id {} deleted", id);
    }

    public Set<Category> buildCategories(Set<Long> categoryIds, List<String> newCategories) {
        log.info("CategoryService.buildCategories called categoryIdsCount={} newCategoriesCount={}", categoryIds != null ? categoryIds.size() : 0, newCategories != null ? newCategories.size() : 0);
        Set<Category> categories = new HashSet<>();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            categories.addAll(categoryRepository.findAllById(categoryIds));
        }
        if (newCategories != null) {
            for (String name : newCategories) {
                if (name == null || name.isBlank()) continue;
                Category category = new Category();
                category.setName(name);
                category.setDescription("");
                categories.add(categoryRepository.save(category));
            }
        }
        return categories;
    }
}
