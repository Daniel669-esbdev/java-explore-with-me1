package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Creating category: {}", categoryDto.getName());
        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();
        return toDto(repository.save(category));
    }

    public void deleteCategory(Long catId) {
        log.info("Deleting category id={}", catId);
        if (!repository.existsById(catId)) {
            throw new NotFoundException("Category with id=" + catId + " was not found");
        }
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("The category is not empty");
        }
        repository.deleteById(catId);
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Updating category id={}: new name={}", catId, categoryDto.getName());
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        category.setName(categoryDto.getName());
        return toDto(repository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Getting categories: from={}, size={}", from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return repository.findAll(pageRequest)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        log.info("Getting category by id: {}", catId);
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        return toDto(category);
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}