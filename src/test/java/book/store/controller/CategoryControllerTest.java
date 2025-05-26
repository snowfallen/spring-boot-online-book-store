package book.store.controller;

import static book.store.util.TestUtil.TEST_CATEGORY_DESCRIPTION;
import static book.store.util.TestUtil.TEST_CATEGORY_ID;
import static book.store.util.TestUtil.TEST_CATEGORY_NAME;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.category.CreateCategoryRequestDto;
import book.store.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/database/controller/insert-default-category.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/database/controller/delete-all-books.sql",
        "/database/controller/delete-all-categories.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class CategoryControllerTest {

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$[0].name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$[0].description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser
    @DisplayName("Get category by id returns category")
    void getCategoryById_ReturnsCategory() throws Exception {
        mockMvc.perform(get("/categories/{id}", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @SqlMergeMode(SqlMergeMode.MergeMode.OVERRIDE)
    @Sql(scripts = {"/database/controller/delete-all-books.sql",
            "/database/controller/delete-all-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create category with valid data returns created category")
    void createCategory_WithValidData_ReturnsCreatedCategory() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createValidCategoryRequestDto();

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(requestDto.getName()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
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
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update category returns updated category")
    void updateCategory_ReturnsUpdatedCategory() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createValidCategoryRequestDto();
        requestDto.setName("Updated Category Name");

        mockMvc.perform(put("/categories/{id}", TEST_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$.name").value(requestDto.getName()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete category returns deleted category")
    void deleteCategory_ReturnsDeletedCategory() throws Exception {
        mockMvc.perform(delete("/categories/{id}", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CATEGORY_ID.intValue()))
                .andExpect(jsonPath("$.name").value(TEST_CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CATEGORY_DESCRIPTION));
    }

    @Test
    @WithMockUser
    @DisplayName("Get books by category id returns list of books")
    @Sql(scripts = "/database/controller/insert-default-book-with-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getBooksByCategoryId_ReturnsListOfBooks() throws Exception {
        mockMvc.perform(get("/categories/{id}/books", TEST_CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TestUtil.TEST_BOOK_ID))
                .andExpect(jsonPath("$[0].title").value(TestUtil.TEST_BOOK_TITLE))
                .andExpect(jsonPath("$[0].author").value(TestUtil.TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$[0].isbn").value(TestUtil.TEST_BOOK_ISBN))
                .andExpect(jsonPath("$[0].price").value(TestUtil.TEST_BOOK_PRICE.doubleValue()))
                .andExpect(jsonPath("$[0].description").value(TestUtil.TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$[0].coverImage").value(TestUtil.TEST_BOOK_COVER_IMAGE));
    }
}
