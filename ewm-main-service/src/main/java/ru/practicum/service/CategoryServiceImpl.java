package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Adding new category: name={}", newCategoryDto.getName());
        Category category = Category.builder()
                .name(newCategoryDto.getName())
                .build();
        Category savedCategory = repository.save(category);
        log.info("Category saved with id={}", savedCategory.getId());
        return toCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("Deleting category with id={}", catId);
        if (!repository.existsById(catId)) {
            log.error("Category with id={} not found for deletion", catId);
            throw new NotFoundException("Category with id=" + catId + " was not found");
        }
        if (eventRepository.existsByCategoryId(catId)) {
            log.error("Category with id={} is not empty and cannot be deleted", catId);
            throw new ConflictException("The category is not empty");
        }
        repository.deleteById(catId);
        log.info("Category with id={} deleted", catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        log.info("Updating category with id={}: newName={}", catId, newCategoryDto.getName());
        Category category = repository.findById(catId)
                .orElseThrow(() -> {
                    log.error("Category with id={} not found for update", catId);
                    return new NotFoundException("Category with id=" + catId + " was not found");
                });

        Category existingCategory = repository.findByName(newCategoryDto.getName());
        if (existingCategory != null && !existingCategory.getId().equals(catId)) {
            throw new ConflictException("Category with this name already exists");
        }

        category.setName(newCategoryDto.getName());
        Category updatedCategory = repository.save(category);
        log.info("Category with id={} updated", catId);
        return toCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Fetching categories: from={}, size={}", from, size);
        PageRequest page = PageRequest.of(from / size, size);
        List<Category> categories = repository.findAll(page).getContent();
        log.info("Found {} categories", categories.size());
        return categories.stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        log.info("Fetching category with id={}", catId);
        Category category = repository.findById(catId)
                .orElseThrow(() -> {
                    log.error("Category with id={} not found", catId);
                    return new NotFoundException("Category with id=" + catId + " was not found");
                });
        return toCategoryDto(category);
    }

    private CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}