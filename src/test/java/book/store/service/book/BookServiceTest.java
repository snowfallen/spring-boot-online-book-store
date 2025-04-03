package book.store.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import book.store.service.book.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final Long TEST_BOOK_ID = 1L;
    private static final Long TEST_CATEGORY_ID = 1L;
    private static final String TEST_BOOK_TITLE = "Test Book";
    private static final String TEST_BOOK_AUTHOR = "Test Author";
    private static final String TEST_BOOK_ISBN = "1234567890123";
    private static final BigDecimal TEST_BOOK_PRICE = BigDecimal.valueOf(19.99);
    private static final String TEST_BOOK_DESCRIPTION = "Test Description";
    private static final String TEST_BOOK_COVER_IMAGE = "test.jpg";
    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 0;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Get book by id when book exists")
    void getBookById_WithExistingBook_ReturnsBookDto() {
        Book book = createTestBook();
        BookDto expectedDto = createTestBookDto();

        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actual = bookService.getBookDtoById(TEST_BOOK_ID);

        assertEquals(expectedDto, actual);
    }

    @Test
    @DisplayName("Get book by id when book doesn't exist throws exception")
    void getBookById_WithNonExistingBook_ThrowsException() {
        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookService.getBookDtoById(TEST_BOOK_ID));
    }

    @Test
    @DisplayName("Create book with valid data returns created book")
    void createBook_WithValidData_ReturnsCreatedBook() {
        CreateBookRequestDto requestDto = createTestBookRequestDto();
        Book book = createTestBook();
        BookDto expectedDto = createTestBookDto();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actual = bookService.save(requestDto);

        assertEquals(expectedDto, actual);
    }

    @Test
    @DisplayName("Get all books returns page of books")
    void getAllBooks_ReturnsPageOfBooks() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Book> books = List.of(createTestBook(), createTestBook());
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        List<BookDto> expectedDtos = List.of(createTestBookDto(), createTestBookDto());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoList(bookPage)).thenReturn(expectedDtos);

        List<BookDto> actual = bookService.getAll(pageable);

        assertEquals(expectedDtos.size(), actual.size());
        assertEquals(expectedDtos, actual);
    }

    @Test
    @DisplayName("Search books by parameters")
    void search_WithValidParameters_ReturnsBooks() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{TEST_BOOK_TITLE},
                new String[]{TEST_BOOK_AUTHOR}
        );
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Book> books = List.of(createTestBook());
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        List<BookDto> expectedDtos = List.of(createTestBookDto());

        Specification<Book> mockSpec = mock(Specification.class);
        when(bookSpecificationBuilder.build(any(BookSearchParametersDto.class)))
                .thenReturn(mockSpec);
        when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bookPage);
        when(bookMapper.toDtoList(any(Page.class))).thenReturn(expectedDtos);

        List<BookDto> actual = bookService.search(params, pageable);

        assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("Get books by category id")
    void getBooksByCategoryId_WithValidId_ReturnsBooks() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Book> books = List.of(createTestBook());
        BookDtoWithoutCategoryIds expectedDto = createTestBookDtoWithoutCategoryIds();

        when(bookRepository.findAllBooksByCategoryId(TEST_CATEGORY_ID, pageable)).thenReturn(books);
        when(bookMapper.toBookDtoWithoutCategoryIdsList(books))
                .thenReturn(List.of(expectedDto));

        List<BookDtoWithoutCategoryIds> actual =
                bookService.getBooksByCategoryId(TEST_CATEGORY_ID, pageable);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(expectedDto, actual.get(0));
    }

    @Test
    @DisplayName("Update book updates all fields")
    void updateBook_UpdatesAllFields() {
        Book book = createTestBook();
        CreateBookRequestDto requestDto = createTestBookRequestDto();
        BookDto expectedDto = createTestBookDto();

        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actual = bookService.updateById(TEST_BOOK_ID, requestDto);

        assertEquals(expectedDto, actual);
        assertEquals(TEST_BOOK_TITLE, book.getTitle());
        assertEquals(TEST_BOOK_AUTHOR, book.getAuthor());
        assertEquals(TEST_BOOK_ISBN, book.getIsbn());
        assertEquals(TEST_BOOK_DESCRIPTION, book.getDescription());
        assertEquals(TEST_BOOK_COVER_IMAGE, book.getCoverImage());
    }

    @Test
    @DisplayName("Delete book by id")
    void deleteById_WithExistingBook_ReturnsDeletedBook() {
        Book book = createTestBook();
        BookDto expectedDto = createTestBookDto();

        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actual = bookService.deleteById(TEST_BOOK_ID);

        assertEquals(expectedDto, actual);
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setId(TEST_BOOK_ID);
        book.setTitle(TEST_BOOK_TITLE);
        book.setAuthor(TEST_BOOK_AUTHOR);
        book.setIsbn(TEST_BOOK_ISBN);
        book.setPrice(TEST_BOOK_PRICE);
        book.setDescription(TEST_BOOK_DESCRIPTION);
        book.setCoverImage(TEST_BOOK_COVER_IMAGE);
        return book;
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
        return bookDto;
    }

    private BookDtoWithoutCategoryIds createTestBookDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(TEST_BOOK_ID);
        bookDto.setTitle(TEST_BOOK_TITLE);
        bookDto.setAuthor(TEST_BOOK_AUTHOR);
        bookDto.setIsbn(TEST_BOOK_ISBN);
        bookDto.setPrice(TEST_BOOK_PRICE);
        bookDto.setDescription(TEST_BOOK_DESCRIPTION);
        bookDto.setCoverImage(TEST_BOOK_COVER_IMAGE);
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
        requestDto.setCategoryIds(Set.of(1L, 2L));
        return requestDto;
    }
}
