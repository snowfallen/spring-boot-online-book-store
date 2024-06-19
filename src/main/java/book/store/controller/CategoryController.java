package book.store.controller;

import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.service.book.BookService;
import book.store.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Categories management", description = "Endpoints for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @Operation(summary = "Create a category")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto createCategory(@RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @Operation(summary = "Get all categories")
    @GetMapping
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryService.getAll(pageable);
    }

    @Operation(summary = "Get category by id")
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Update category by id")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.update(id, requestDto);
    }

    @Operation(summary = "Delete category by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto deleteCategoryById(@PathVariable Long id) {
        return categoryService.deleteById(id);
    }

    @Operation(summary = "Get books by category id")
    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id,
                                                                Pageable pageable) {
        return bookService.getBooksByCategoryId(id, pageable);
    }
}
