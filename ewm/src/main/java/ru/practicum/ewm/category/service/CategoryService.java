package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.model.Category;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    Category addCategory(Category category);

    Category updateCategory(Long categoryId, Category category);

    void removeCategory(Long categoryId);

    List<Category> getAllCategories(Pageable page);

    Category getCategoryById(Long categoryId);

}
