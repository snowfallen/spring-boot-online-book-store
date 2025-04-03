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

import book.store.dto.book.BookDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.service.book.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
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
class BookControllerTest {
    private static final Long TEST_BOOK_ID = 1L;
    private static final String TEST_BOOK_TITLE = "Test Book";
    private static final String TEST_BOOK_AUTHOR = "Test Author";
    private static final String TEST_BOOK_ISBN = "1234567890123";
    private static final BigDecimal TEST_BOOK_PRICE = new BigDecimal("19.99");
    private static final String TEST_BOOK_DESCRIPTION = "Test Description";
    private static final String TEST_BOOK_COVER_IMAGE = "test.jpg";
    private static final HashSet<Long> TEST_BOOK_CATEGORY_IDS
            = new HashSet<>(Arrays.asList(1L, 2L));

    @Autowired
    private static MockMvc mockMvc;

    @MockBean
    private BookService bookService;
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
        BookDto bookDto = createTestBookDto();
        when(bookService.getAll(any())).thenReturn(List.of(bookDto));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$[0].title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$[0].author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$[0].isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$[0].price").value(TEST_BOOK_PRICE.toString()))
                .andExpect(jsonPath("$[0].description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$[0].coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$[0].categoryIds").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("Get book by id returns book")
    void getBookById_ReturnsBook() throws Exception {
        BookDto bookDto = createTestBookDto();
        when(bookService.getBookDtoById(TEST_BOOK_ID)).thenReturn(bookDto);

        mockMvc.perform(get("/books/{id}", TEST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$.title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$.isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$.price").value(TEST_BOOK_PRICE.toString()))
                .andExpect(jsonPath("$.description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$.coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create book returns created book")
    void createBook_ReturnsCreatedBook() throws Exception {
        CreateBookRequestDto requestDto = createTestBookRequestDto();
        BookDto bookDto = createTestBookDto();

        when(bookService.save(any(CreateBookRequestDto.class))).thenReturn(bookDto);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$.title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$.isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$.price").value(TEST_BOOK_PRICE.toString()))
                .andExpect(jsonPath("$.description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$.coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update book returns updated book")
    void updateBook_ReturnsUpdatedBook() throws Exception {
        CreateBookRequestDto requestDto = createTestBookRequestDto();
        BookDto bookDto = createTestBookDto();

        when(bookService.updateById(any(Long.class), any(CreateBookRequestDto.class)))
                .thenReturn(bookDto);

        mockMvc.perform(put("/books/{id}", TEST_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$.title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$.isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$.price").value(TEST_BOOK_PRICE.toString()))
                .andExpect(jsonPath("$.description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$.coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete book returns deleted book")
    void deleteBook_ReturnsDeletedBook() throws Exception {
        BookDto bookDto = createTestBookDto();
        when(bookService.deleteById(TEST_BOOK_ID)).thenReturn(bookDto);

        mockMvc.perform(delete("/books/{id}", TEST_BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$.title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$.author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$.isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$.price").value(TEST_BOOK_PRICE.toString()))
                .andExpect(jsonPath("$.description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$.coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$.categoryIds").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("Search books returns list of books")
    void search_ReturnsListOfBooks() throws Exception {
        BookDto bookDto = createTestBookDto();
        when(bookService.search(any(), any())).thenReturn(List.of(bookDto));

        mockMvc.perform(get("/books/search")
                .param("titles", TEST_BOOK_TITLE)
                .param("authors", TEST_BOOK_AUTHOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_BOOK_ID))
                .andExpect(jsonPath("$[0].title").value(TEST_BOOK_TITLE))
                .andExpect(jsonPath("$[0].author").value(TEST_BOOK_AUTHOR))
                .andExpect(jsonPath("$[0].isbn").value(TEST_BOOK_ISBN))
                .andExpect(jsonPath("$[0].price").value(TEST_BOOK_PRICE.toString()))
                .andExpect(jsonPath("$[0].description").value(TEST_BOOK_DESCRIPTION))
                .andExpect(jsonPath("$[0].coverImage").value(TEST_BOOK_COVER_IMAGE))
                .andExpect(jsonPath("$[0].categoryIds").isArray());
    }

    private BookDto createTestBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(TEST_BOOK_ID);
        bookDto.setTitle(TEST_BOOK_TITLE);
        bookDto.setAuthor(TEST_BOOK_AUTHOR);
        bookDto.setIsbn(TEST_BOOK_ISBN);
        bookDto.setPrice(TEST_BOOK_PRICE);
        bookDto.setDescription(TEST_BOOK_DESCRIPTION);
        bookDto.setCoverImage(TEST_BOOK_COVER_IMAGE);
        bookDto.setCategoryIds(TEST_BOOK_CATEGORY_IDS);
        return bookDto;
    }

    private CreateBookRequestDto createTestBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(TEST_BOOK_TITLE);
        requestDto.setAuthor(TEST_BOOK_AUTHOR);
        requestDto.setIsbn(TEST_BOOK_ISBN);
        requestDto.setPrice(TEST_BOOK_PRICE);
        requestDto.setDescription(TEST_BOOK_DESCRIPTION);
        requestDto.setCoverImage(TEST_BOOK_COVER_IMAGE);
        requestDto.setCategoryIds(TEST_BOOK_CATEGORY_IDS);
        return requestDto;
    }
}
