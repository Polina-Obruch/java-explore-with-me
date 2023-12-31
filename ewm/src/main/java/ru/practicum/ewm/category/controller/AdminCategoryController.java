package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info("Запрос на создание категории");
        return categoryMapper.toCategoryDto(
                categoryService.addCategory(categoryMapper.toCategory(categoryRequestDto)));
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateCategory(@PathVariable Long categoryId,
                                      @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info("Запрос на обновление категории");
        return categoryMapper.toCategoryDto(categoryService.updateCategory(
                categoryId, categoryMapper.toCategory(categoryRequestDto)));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("Запрос на удаление категории");
        categoryService.removeCategory(categoryId);
    }

}
