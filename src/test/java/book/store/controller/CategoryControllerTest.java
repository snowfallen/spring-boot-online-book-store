package book.store.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    private static final Long TEST_CATEGORY_ID = 1L;
    private static final String TEST_CATEGORY_NAME = "Test Category";
    private static final String TEST_CATEGORY_DESCRIPTION = "Test Description";

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
        CategoryDto categoryDto = createTestCategoryDto();
        when(categoryService.getAll(any())).thenReturn(List.of(categoryDto));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_CATEGORY_ID))
                .andExpect(jsonPath("$[0].name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$[0].description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser
    @DisplayName("Get category by id returns category")
    void getCategoryById_ReturnsCategory() throws Exception {
        CategoryDto categoryDto = createTestCategoryDto();
        when(categoryService.getById(any(Long.class))).thenReturn(categoryDto);

        mockMvc.perform(get("/categories/{id}", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create category returns created category")
    void createCategory_ReturnsCreatedCategory() throws Exception {
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto();
        CategoryDto categoryDto = createTestCategoryDto();

        when(categoryService.save(any(CreateCategoryRequestDto.class))).thenReturn(categoryDto);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update category returns updated category")
    void updateCategory_ReturnsUpdatedCategory() throws Exception {
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto();
        CategoryDto categoryDto = createTestCategoryDto();

        when(categoryService.update(any(Long.class), any(CreateCategoryRequestDto.class)))
                .thenReturn(categoryDto);

        mockMvc.perform(put("/categories/{id}", TEST_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete category returns deleted category")
    void deleteCategory_ReturnsDeletedCategory() throws Exception {
        CategoryDto categoryDto = createTestCategoryDto();
        when(categoryService.deleteById(TEST_CATEGORY_ID)).thenReturn(categoryDto);

        mockMvc.perform(delete("/categories/{id}", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser
    @DisplayName("Get books by category id returns list of books")
    void getBooksByCategoryId_ReturnsListOfBooks() throws Exception {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(TEST_CATEGORY_ID);
        bookDto.setTitle("Test Book");
        bookDto.setAuthor("Test Author");
        bookDto.setIsbn("1234567890123");
        bookDto.setPrice(new java.math.BigDecimal("19.99"));
        bookDto.setDescription("Test Description");
        bookDto.setCoverImage("test.jpg");

        when(bookService.getBooksByCategoryId(any(Long.class), any()))
                .thenReturn(List.of(bookDto));

        mockMvc.perform(get("/categories/{id}/books", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_CATEGORY_ID))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author").value("Test Author"))
                .andExpect(jsonPath("$[0].isbn").value("1234567890123"))
                .andExpect(jsonPath("$[0].price").value("19.99"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].coverImage").value("test.jpg"));
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
}
