package book.store.service.category;

import static book.store.util.TestUtil.TEST_CATEGORY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.category.CategoryRepository;
import book.store.service.category.impl.CategoryServiceImpl;
import book.store.util.TestUtil;
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
    private static final String UPDATED_CATEGORY_NAME = "Updated Test Category";
    private static final String UPDATED_CATEGORY_DESCRIPTION = "Updated Test Description";
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
        Category categoryFromRepo = TestUtil.createTestCategory();
        categoryFromRepo.setId(TEST_CATEGORY_ID);
        CategoryDto expectedDto = TestUtil.createTestCategoryDto();

        when(categoryRepository.findById(TEST_CATEGORY_ID))
                .thenReturn(Optional.of(categoryFromRepo)
                );
        when(categoryMapper.toDto(categoryFromRepo)).thenReturn(expectedDto);

        CategoryDto actual = categoryService.getById(TEST_CATEGORY_ID);

        assertEquals(expectedDto, actual);
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryMapper, times(1)).toDto(categoryFromRepo);
    }

    @Test
    @DisplayName("Get category by id when category doesn't exist throws exception")
    void getCategoryById_WithNonExistingCategory_ThrowsException() {
        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService
                .getById(TEST_CATEGORY_ID)
        );
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    @DisplayName("Create category with valid data returns created category")
    void createCategory_WithValidData_ReturnsCreatedCategory() {
        CreateCategoryRequestDto requestDto = TestUtil.createValidCategoryRequestDto();
        Category categoryToSave = TestUtil.createTestCategory();
        Category savedCategory = TestUtil.createTestCategory();
        savedCategory.setId(TEST_CATEGORY_ID);
        CategoryDto expectedDto = TestUtil.createTestCategoryDto();

        when(categoryMapper.toModel(requestDto)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expectedDto);

        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(expectedDto, actual);
        verify(categoryMapper, times(1)).toModel(requestDto);
        verify(categoryRepository, times(1)).save(categoryToSave);
        verify(categoryMapper, times(1)).toDto(savedCategory);
    }

    @Test
    @DisplayName("Get all categories returns page of categories")
    void getAllCategories_ReturnsPageOfCategories() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Category testCategory = TestUtil.createTestCategory();
        testCategory.setId(TEST_CATEGORY_ID);
        List<Category> categories = List.of(testCategory);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        List<CategoryDto> expectedDtos = List.of(TestUtil.createTestCategoryDto());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDtoList(categoryPage)).thenReturn(expectedDtos);

        List<CategoryDto> actual = categoryService.getAll(pageable);

        assertEquals(expectedDtos.size(), actual.size());
        assertEquals(expectedDtos, actual);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDtoList(categoryPage);
    }

    @Test
    @DisplayName("Update category with valid data returns updated category")
    void updateCategory_WithValidData_ReturnsUpdatedCategory() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(UPDATED_CATEGORY_NAME);
        requestDto.setDescription(UPDATED_CATEGORY_DESCRIPTION);

        Category existingCategory = TestUtil.createTestCategory();
        existingCategory.setId(TEST_CATEGORY_ID);

        Category updatedCategory = new Category();
        updatedCategory.setId(TEST_CATEGORY_ID);
        updatedCategory.setName(UPDATED_CATEGORY_NAME);
        updatedCategory.setDescription(UPDATED_CATEGORY_DESCRIPTION);

        CategoryDto expectedDto = new CategoryDto(
                TEST_CATEGORY_ID.intValue(),
                UPDATED_CATEGORY_NAME,
                UPDATED_CATEGORY_DESCRIPTION
        );

        when(categoryRepository.findById(TEST_CATEGORY_ID))
                .thenReturn(Optional.of(existingCategory)
                );
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(expectedDto);

        CategoryDto actual = categoryService.update(TEST_CATEGORY_ID, requestDto);

        assertEquals(expectedDto, actual);
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toDto(updatedCategory);
    }

    @Test
    @DisplayName("Update category updates all fields")
    void updateCategory_UpdatesAllFields() {
        Category category = TestUtil.createTestCategory();
        category.setId(TEST_CATEGORY_ID);

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(UPDATED_CATEGORY_NAME);
        requestDto.setDescription(UPDATED_CATEGORY_DESCRIPTION);

        CategoryDto dummyDto = new CategoryDto(TEST_CATEGORY_ID.intValue(),
                UPDATED_CATEGORY_NAME, UPDATED_CATEGORY_DESCRIPTION);

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dummyDto);

        categoryService.update(TEST_CATEGORY_ID, requestDto);

        assertEquals(UPDATED_CATEGORY_NAME, category.getName());
        assertEquals(UPDATED_CATEGORY_DESCRIPTION, category.getDescription());
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("Update non-existing category throws exception")
    void updateCategory_WithNonExistingCategory_ThrowsException() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(UPDATED_CATEGORY_NAME);
        requestDto.setDescription(UPDATED_CATEGORY_DESCRIPTION);

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(TEST_CATEGORY_ID, requestDto));
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    @DisplayName("Delete category when category exists returns deleted category")
    void deleteCategory_WithExistingCategory_ReturnsDeletedCategory() {
        Category categoryEntity = TestUtil.createTestCategory();
        categoryEntity.setId(TEST_CATEGORY_ID);

        CategoryDto expectedDto = TestUtil.createTestCategoryDto();

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(categoryEntity));
        when(categoryMapper.toDto(argThat(cat ->
                cat.getId().equals(TEST_CATEGORY_ID) && cat.isDeleted()
        ))).thenReturn(expectedDto);

        CategoryDto actual = categoryService.deleteById(TEST_CATEGORY_ID);

        assertEquals(expectedDto, actual);
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryRepository, times(1)).delete(categoryEntity);
        verify(categoryMapper, times(1)).toDto(argThat(cat ->
                cat.getId().equals(TEST_CATEGORY_ID) && cat.isDeleted()
        ));
    }

    @Test
    @DisplayName("Delete category sets isDeleted to true")
    void deleteCategory_SetsIsDeletedTrue() {
        Category category = TestUtil.createTestCategory();
        category.setId(TEST_CATEGORY_ID);
        CategoryDto dummyDto = TestUtil.createTestCategoryDto();

        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(argThat(Category::isDeleted))).thenReturn(dummyDto);

        categoryService.deleteById(TEST_CATEGORY_ID);

        assertTrue(category.isDeleted());
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryRepository, times(1)).delete(category);
        verify(categoryMapper, times(1)).toDto(argThat(Category::isDeleted));
    }

    @Test
    @DisplayName("Delete non-existing category throws exception")
    void deleteCategory_WithNonExistingCategory_ThrowsException() {
        when(categoryRepository.findById(TEST_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(TEST_CATEGORY_ID));
        verify(categoryRepository, times(1)).findById(TEST_CATEGORY_ID);
        verify(categoryRepository, never()).delete(any(Category.class));
        verify(categoryMapper, never()).toDto(any(Category.class));
    }
}
