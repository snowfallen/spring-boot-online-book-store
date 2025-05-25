package book.store.controller;

import static book.store.util.TestUtil.TEST_CATEGORY_DESCRIPTION;
import static book.store.util.TestUtil.TEST_CATEGORY_ID;
import static book.store.util.TestUtil.TEST_CATEGORY_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.service.book.BookService;
import book.store.service.category.CategoryService;
import book.store.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

    @Autowired
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private BookService bookService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("Get all categories returns list of categories")
    void getAllCategories_ReturnsListOfCategories() throws Exception {
        CategoryDto categoryDto = TestUtil.createTestCategoryDto();
        when(categoryService.getAll(any(Pageable.class))).thenReturn(List.of(categoryDto));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$[0].name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$[0].description").value(TEST_CATEGORY_DESCRIPTION));

        verify(categoryService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Get category by id returns category")
    void getCategoryById_ReturnsCategory() throws Exception {
        CategoryDto categoryDto = TestUtil.createTestCategoryDto();
        when(categoryService.getById(eq(TEST_CATEGORY_ID))).thenReturn(categoryDto);

        mockMvc.perform(get("/categories/{id}", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));

        verify(categoryService, times(1)).getById(eq(TEST_CATEGORY_ID));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create category with valid data returns created category")
    void createCategory_WithValidData_ReturnsCreatedCategory() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createValidCategoryRequestDto();
        CategoryDto categoryDto = TestUtil.createTestCategoryDto();

        when(categoryService.save(any(CreateCategoryRequestDto.class))).thenReturn(categoryDto);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));

        verify(categoryService, times(1)).save(any(CreateCategoryRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create category with null name returns bad request")
    void createCategory_WithNullName_ReturnsBadRequest() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createInvalidCategoryRequestDtoNullName();

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).save(any(CreateCategoryRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create category with empty name returns bad request")
    void createCategory_WithEmptyName_ReturnsBadRequest() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createInvalidCategoryRequestDtoEmptyName();

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).save(any(CreateCategoryRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update category returns updated category")
    void updateCategory_ReturnsUpdatedCategory() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createValidCategoryRequestDto();
        CategoryDto categoryDto = TestUtil.createTestCategoryDto();

        when(categoryService.update(eq(TEST_CATEGORY_ID), any(CreateCategoryRequestDto.class)))
                .thenReturn(categoryDto);

        mockMvc.perform(put("/categories/{id}", TEST_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));

        verify(categoryService, times(1))
                .update(eq(TEST_CATEGORY_ID), any(CreateCategoryRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete category returns deleted category")
    void deleteCategory_ReturnsDeletedCategory() throws Exception {
        CategoryDto categoryDto = TestUtil.createTestCategoryDto();
        when(categoryService.deleteById(TEST_CATEGORY_ID)).thenReturn(categoryDto);

        mockMvc.perform(delete("/categories/{id}", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));

        verify(categoryService, times(1)).deleteById(eq(TEST_CATEGORY_ID));
    }

    @Test
    @WithMockUser
    @DisplayName("Get books by category id returns list of books")
    void getBooksByCategoryId_ReturnsListOfBooks() throws Exception {
        BookDtoWithoutCategoryIds bookDto = TestUtil.createTestBookDtoWithoutCategoryIds();
        when(bookService.getBooksByCategoryId(eq(TEST_CATEGORY_ID), any(Pageable.class)))
                .thenReturn(List.of(bookDto));

        mockMvc.perform(get("/categories/{id}/books", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TestUtil.TEST_BOOK_ID))
                .andExpect(jsonPath("$[0].title").value(TestUtil.TEST_BOOK_TITLE))
                .andExpect(jsonPath("$[0].author").value(TestUtil.TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$[0].isbn").value(TestUtil.TEST_BOOK_ISBN))
                .andExpect(jsonPath("$[0].price").value(TestUtil.TEST_BOOK_PRICE.doubleValue()))
                .andExpect(jsonPath("$[0].description").value(TestUtil.TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$[0].coverImage").value(TestUtil.TEST_BOOK_COVER_IMAGE));

        verify(bookService, times(1))
                .getBooksByCategoryId(eq(TEST_CATEGORY_ID), any(Pageable.class));
    }
}
