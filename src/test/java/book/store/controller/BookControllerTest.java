package book.store.controller;

import static book.store.util.TestUtil.TEST_BOOK_AUTHOR;
import static book.store.util.TestUtil.TEST_BOOK_COVER_IMAGE;
import static book.store.util.TestUtil.TEST_BOOK_DESCRIPTION;
import static book.store.util.TestUtil.TEST_BOOK_ID;
import static book.store.util.TestUtil.TEST_BOOK_ISBN;
import static book.store.util.TestUtil.TEST_BOOK_PRICE;
import static book.store.util.TestUtil.TEST_BOOK_TITLE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.CreateBookRequestDto;
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
@Sql(scripts = "/database/controller/insert-default-book.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/database/controller/delete-all-books.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookControllerTest {

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
    @DisplayName("Get all books returns list of books")
    void getAllBooks_ReturnsListOfBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$[0].title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$[0].author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$[0].isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$[0].price").value(TEST_BOOK_PRICE.doubleValue()))
                .andExpect(jsonPath("$[0].description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$[0].coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$[0].categoryIds").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("Get book by id returns book")
    void getBookById_ReturnsBook() throws Exception {
        mockMvc.perform(get("/books/{id}", TEST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$.title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$.isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$.price").value(TEST_BOOK_PRICE.doubleValue()))
                .andExpect(jsonPath("$.description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$.coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @SqlMergeMode(SqlMergeMode.MergeMode.OVERRIDE)
    @Sql(scripts = {"/database/controller/delete-all-books.sql",
            "/database/controller/insert-default-category.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Create book with valid data returns created book")
    void createBook_WithValidData_ReturnsCreatedBook() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createValidBookRequestDto();

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.author").value(requestDto.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(requestDto.getIsbn()))
                .andExpect(jsonPath("$.price").value(requestDto.getPrice().toString()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.coverImage").value(requestDto.getCoverImage()))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create book with null title returns bad request")
    void createBook_WithNullTitle_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createInvalidBookRequestDtoNullTitle();

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create book with negative price returns bad request")
    void createBook_WithNegativePrice_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createInvalidBookRequestDtoNegativePrice();

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create book with invalid ISBN returns bad request")
    void createBook_WithInvalidIsbn_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createInvalidBookRequestDtoInvalidIsbn();

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update book returns updated book")
    void updateBook_ReturnsUpdatedBook() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createValidBookRequestDto();
        requestDto.setTitle("Updated Test Book Title");

        mockMvc.perform(put("/books/{id}", TEST_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$.title").value(requestDto.getTitle()))
                .andExpect(jsonPath("$.author").value(requestDto.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(requestDto.getIsbn()))
                .andExpect(jsonPath("$.price").value(requestDto.getPrice().toString()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.coverImage").value(requestDto.getCoverImage()))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete book returns deleted book")
    void deleteBook_ReturnsDeletedBook() throws Exception {
        mockMvc.perform(delete("/books/{id}", TEST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$.title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$.isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$.price").value(TEST_BOOK_PRICE.doubleValue()))
                .andExpect(jsonPath("$.description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$.coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("Search books returns list of books")
    void search_ReturnsListOfBooks() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("titles", TEST_BOOK_TITLE)
                        .param("authors", TEST_BOOK_AUTHOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$[0].title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$[0].author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$[0].isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$[0].price").value(TEST_BOOK_PRICE.doubleValue()))
                .andExpect(jsonPath("$[0].description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$[0].coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$[0].categoryIds").isArray());
    }
}
