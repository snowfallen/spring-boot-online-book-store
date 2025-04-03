package book.store.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.category.CategoryRepository;
import book.store.service.category.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final Long TEST_CATEGORY_ID = 1L;
    private static final String TEST_CATEGORY_NAME = "Test Category";
    private static final String TEST_CATEGORY_DESCRIPTION = "Test Description";
    private static final String UPDATED_CATEGORY_NAME = "Updated Category";
    private static final String UPDATED_CATEGORY_DESCRIPTION = "Updated Description";
    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 0;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Get category by id when category exists")
    void getCategoryById_WithExistingCategory_ReturnsCategoryDto() {
        Category category = createTestCategory();
        CategoryDto expectedDto = createTestCategoryDto();

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryDto actual = categoryService.getById(TEST_CATEGORY_ID);

        assertEquals(expectedDto, actual);
    }

    @Test
    @DisplayName("Get category by id when category doesn't exist throws exception")
    void getCategoryById_WithNonExistingCategory_ThrowsException() {
        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService
                .getById(TEST_CATEGORY_ID)
        );
    }

    @Test
    @DisplayName("Create category with valid data returns created category")
    void createCategory_WithValidData_ReturnsCreatedCategory() {
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto();
        Category category = createTestCategory();
        CategoryDto expectedDto = createTestCategoryDto();

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(expectedDto, actual);
    }

    @Test
    @DisplayName("Get all categories returns page of categories")
    void getAllCategories_ReturnsPageOfCategories() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Category> categories = List.of(createTestCategory(), createTestCategory());
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        List<CategoryDto> expectedDtos = List.of(createTestCategoryDto(), createTestCategoryDto());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDtoList(any(Page.class))).thenReturn(expectedDtos);

        List<CategoryDto> actual = categoryService.getAll(pageable);

        assertEquals(expectedDtos.size(), actual.size());
        assertEquals(expectedDtos, actual);
    }

    @Test
    @DisplayName("Update category with valid data returns updated category")
    void updateCategory_WithValidData_ReturnsUpdatedCategory() {
        CreateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        Category category = createTestCategory();
        CategoryDto expectedDto = createTestCategoryDto();

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryDto actual = categoryService.update(TEST_CATEGORY_ID, requestDto);

        assertEquals(expectedDto, actual);
    }

    @Test
    @DisplayName("Update category updates all fields")
    void updateCategory_UpdatesAllFields() {
        Category category = createTestCategory();
        CreateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryService.update(TEST_CATEGORY_ID, requestDto);

        assertEquals(UPDATED_CATEGORY_NAME, category.getName());
        assertEquals(UPDATED_CATEGORY_DESCRIPTION, category.getDescription());
    }

    @Test
    @DisplayName("Update non-existing category throws exception")
    void updateCategory_WithNonExistingCategory_ThrowsException() {
        CreateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(TEST_CATEGORY_ID, requestDto));
    }

    @Test
    @DisplayName("Delete category when category exists returns deleted category")
    void deleteCategory_WithExistingCategory_ReturnsDeletedCategory() {
        Category category = createTestCategory();
        CategoryDto expectedDto = createTestCategoryDto();

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDto actual = categoryService.deleteById(TEST_CATEGORY_ID);

        assertEquals(expectedDto, actual);
    }

    @Test
    @DisplayName("Delete category sets isDeleted to true")
    void deleteCategory_SetsIsDeletedTrue() {
        Category category = createTestCategory();
        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryService.deleteById(TEST_CATEGORY_ID);

        assertTrue(category.isDeleted());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Delete non-existing category throws exception")
    void deleteCategory_WithNonExistingCategory_ThrowsException() {
        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(TEST_CATEGORY_ID));
    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setId(TEST_CATEGORY_ID);
        category.setName(TEST_CATEGORY_NAME);
        category.setDescription(TEST_CATEGORY_DESCRIPTION);
        return category;
    }

    private CategoryDto createTestCategoryDto() {
        return new CategoryDto(
                (int) TEST_CATEGORY_ID.longValue(),
                TEST_CATEGORY_NAME,
                TEST_CATEGORY_DESCRIPTION
        );
    }

    private CreateCategoryRequestDto createTestCategoryRequestDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(TEST_CATEGORY_NAME);
        requestDto.setDescription(TEST_CATEGORY_DESCRIPTION);
        return requestDto;
    }

    private CreateCategoryRequestDto createUpdateCategoryRequestDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(UPDATED_CATEGORY_NAME);
        requestDto.setDescription(UPDATED_CATEGORY_DESCRIPTION);
        return requestDto;
    }
}
