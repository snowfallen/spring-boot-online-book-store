package book.store.controller;

import static book.store.util.TestUtil.TEST_BOOK_AUTHOR;
import static book.store.util.TestUtil.TEST_BOOK_COVER_IMAGE;
import static book.store.util.TestUtil.TEST_BOOK_DESCRIPTION;
import static book.store.util.TestUtil.TEST_BOOK_ID;
import static book.store.util.TestUtil.TEST_BOOK_ISBN;
import static book.store.util.TestUtil.TEST_BOOK_PRICE;
import static book.store.util.TestUtil.TEST_BOOK_TITLE;
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

import book.store.dto.book.BookDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.service.book.BookService;
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
class BookControllerTest {

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
        BookDto bookDto = TestUtil.createTestBookDto();
        when(bookService.getAll(any(Pageable.class))).thenReturn(List.of(bookDto));

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

        verify(bookService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Get book by id returns book")
    void getBookById_ReturnsBook() throws Exception {
        BookDto bookDto = TestUtil.createTestBookDto();
        when(bookService.getBookDtoById(TEST_BOOK_ID)).thenReturn(bookDto);

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

        verify(bookService, times(1)).getBookDtoById(eq(TEST_BOOK_ID));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create book with valid data returns created book")
    void createBook_WithValidData_ReturnsCreatedBook() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createValidBookRequestDto();
        BookDto bookDto = TestUtil.createTestBookDto();

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

        verify(bookService, times(1)).save(any(CreateBookRequestDto.class));
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

        verify(bookService, never()).save(any(CreateBookRequestDto.class));
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

        verify(bookService, never()).save(any(CreateBookRequestDto.class));
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

        verify(bookService, never()).save(any(CreateBookRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update book returns updated book")
    void updateBook_ReturnsUpdatedBook() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createValidBookRequestDto();
        BookDto bookDto = TestUtil.createTestBookDto();

        when(bookService.updateById(eq(TEST_BOOK_ID), any(CreateBookRequestDto.class)))
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

        verify(bookService, times(1))
                .updateById(eq(TEST_BOOK_ID), any(CreateBookRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete book returns deleted book")
    void deleteBook_ReturnsDeletedBook() throws Exception {
        BookDto bookDto = TestUtil.createTestBookDto();
        when(bookService.deleteById(TEST_BOOK_ID)).thenReturn(bookDto);

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

        verify(bookService, times(1)).deleteById(eq(TEST_BOOK_ID));
    }

    @Test
    @WithMockUser
    @DisplayName("Search books returns list of books")
    void search_ReturnsListOfBooks() throws Exception {
        BookDto bookDto = TestUtil.createTestBookDto();
        when(bookService.search(any(), any(Pageable.class))).thenReturn(List.of(bookDto));

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

        verify(bookService, times(1)).search(any(), any(Pageable.class));
    }
}
