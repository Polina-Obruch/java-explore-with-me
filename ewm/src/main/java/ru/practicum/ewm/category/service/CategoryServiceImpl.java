package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.core.exception.EntityNotFoundException;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public Category addCategory(Category category) {
        log.info("Добавление новой категории");
        return categoryRepository.save(category);
    }

    @Transactional
    @Override
    public Category updateCategory(Long categoryId, Category updateCategory) {
        log.info(String.format("Обновление категории c id = %d", categoryId));
        Category category = this.getCategoryById(categoryId);
        category.setName(updateCategory.getName());
        return category;
    }

    @Transactional
    @Override
    public void removeCategory(Long categoryId) {
        log.info(String.format("Удаление категории c id = %d", categoryId));
        this.getCategoryById(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<Category> getAllCategories(Pageable page) {
        log.info("Выдача всех категорий");
        return categoryRepository.findAll(page).toList();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        log.info(String.format("Выдача категории c id = %d", categoryId));
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
    }
}
