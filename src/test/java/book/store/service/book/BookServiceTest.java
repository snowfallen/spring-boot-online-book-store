package book.store.service.book;

import static book.store.util.TestUtil.TEST_BOOK_AUTHOR;
import static book.store.util.TestUtil.TEST_BOOK_ID;
import static book.store.util.TestUtil.TEST_BOOK_TITLE;
import static book.store.util.TestUtil.TEST_CATEGORY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
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
        Book bookFromRepo = TestUtil.createTestBook();
        bookFromRepo.setId(TEST_BOOK_ID);
        BookDto expectedDto = TestUtil.createTestBookDto();

        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(bookFromRepo));
        when(bookMapper.toDto(bookFromRepo)).thenReturn(expectedDto);

        BookDto actual = bookService.getBookDtoById(TEST_BOOK_ID);

        assertEquals(expectedDto, actual);
        verify(bookRepository, times(1)).findById(TEST_BOOK_ID);
        verify(bookMapper, times(1)).toDto(bookFromRepo);
    }

    @Test
    @DisplayName("Get book by id when book doesn't exist throws exception")
    void getBookById_WithNonExistingBook_ThrowsException() {
        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class, () -> bookService.getBookDtoById(TEST_BOOK_ID)
        );
        verify(bookRepository, times(1)).findById(TEST_BOOK_ID);
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Create book with valid data returns created book")
    void createBook_WithValidData_ReturnsCreatedBook() {
        CreateBookRequestDto requestDto = TestUtil.createValidBookRequestDto();
        Book bookToSave = TestUtil.createTestBook();
        Book savedBook = TestUtil.createTestBook();
        savedBook.setId(TEST_BOOK_ID);
        BookDto expectedDto = TestUtil.createTestBookDto();

        when(bookMapper.toModel(requestDto)).thenReturn(bookToSave);
        when(bookRepository.save(bookToSave)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(expectedDto);

        BookDto actual = bookService.save(requestDto);

        assertEquals(expectedDto, actual);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(bookToSave);
        verify(bookMapper, times(1)).toDto(savedBook);
    }

    @Test
    @DisplayName("Get all books returns page of books")
    void getAllBooks_ReturnsPageOfBooks() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Book testBook = TestUtil.createTestBook();
        testBook.setId(TEST_BOOK_ID);
        List<Book> books = List.of(testBook);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        List<BookDto> expectedDtos = List.of(TestUtil.createTestBookDto());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoList(bookPage)).thenReturn(expectedDtos);

        List<BookDto> actual = bookService.getAll(pageable);

        assertEquals(expectedDtos.size(), actual.size());
        assertEquals(expectedDtos, actual);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDtoList(bookPage);
    }

    @Test
    @DisplayName("Search books by parameters")
    void search_WithValidParameters_ReturnsBooks() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{TEST_BOOK_TITLE},
                new String[]{TEST_BOOK_AUTHOR}
        );
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Book testBook = TestUtil.createTestBook();
        testBook.setId(TEST_BOOK_ID);
        List<Book> books = List.of(testBook);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        List<BookDto> expectedDtos = List.of(TestUtil.createTestBookDto());

        Specification<Book> mockSpec = mock(Specification.class);
        when(bookSpecificationBuilder.build(params)).thenReturn(mockSpec);
        when(bookRepository.findAll(mockSpec, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoList(bookPage)).thenReturn(expectedDtos);

        List<BookDto> actual = bookService.search(params, pageable);

        assertEquals(expectedDtos.size(), actual.size());
        assertEquals(expectedDtos, actual);
        verify(bookSpecificationBuilder, times(1)).build(params);
        verify(bookRepository, times(1)).findAll(mockSpec, pageable);
        verify(bookMapper, times(1)).toDtoList(bookPage);
    }

    @Test
    @DisplayName("Get books by category id")
    void getBooksByCategoryId_WithValidId_ReturnsBooks() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Book testBook = TestUtil.createTestBook();
        testBook.setId(TEST_BOOK_ID);
        List<Book> booksFromRepo = List.of(testBook);
        BookDtoWithoutCategoryIds expectedDto = TestUtil.createTestBookDtoWithoutCategoryIds();

        when(bookRepository.findAllBooksByCategoryId(TEST_CATEGORY_ID, pageable))
                .thenReturn(booksFromRepo);
        when(bookMapper.toBookDtoWithoutCategoryIdsList(booksFromRepo))
                .thenReturn(List.of(expectedDto));

        List<BookDtoWithoutCategoryIds> actual =
                bookService.getBooksByCategoryId(TEST_CATEGORY_ID, pageable);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(expectedDto, actual.get(0));
        verify(bookRepository, times(1))
                .findAllBooksByCategoryId(TEST_CATEGORY_ID, pageable);
        verify(bookMapper, times(1))
                .toBookDtoWithoutCategoryIdsList(booksFromRepo);
    }

    @Test
    @DisplayName("Update book updates all fields")
    void updateBook_UpdatesAllFields() {
        Book existingBook = TestUtil.createTestBook();
        existingBook.setId(TEST_BOOK_ID);

        CreateBookRequestDto requestDto = TestUtil.createValidBookRequestDto();
        Book bookAfterUpdate = TestUtil.createTestBook();
        bookAfterUpdate.setId(TEST_BOOK_ID);
        bookAfterUpdate.setTitle(requestDto.getTitle());
        bookAfterUpdate.setAuthor(requestDto.getAuthor());
        bookAfterUpdate.setIsbn(requestDto.getIsbn());
        bookAfterUpdate.setPrice(requestDto.getPrice());
        bookAfterUpdate.setDescription(requestDto.getDescription());
        bookAfterUpdate.setCoverImage(requestDto.getCoverImage());

        BookDto expectedDto = TestUtil.createTestBookDto();

        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(bookAfterUpdate);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedDto);

        BookDto actual = bookService.updateById(TEST_BOOK_ID, requestDto);

        assertEquals(expectedDto, actual);

        assertEquals(requestDto.getTitle(), existingBook.getTitle());
        assertEquals(requestDto.getAuthor(), existingBook.getAuthor());
        assertEquals(requestDto.getIsbn(), existingBook.getIsbn());
        assertEquals(requestDto.getPrice(), existingBook.getPrice());
        assertEquals(requestDto.getDescription(), existingBook.getDescription());
        assertEquals(requestDto.getCoverImage(), existingBook.getCoverImage());

        verify(bookRepository, times(1)).findById(TEST_BOOK_ID);
        verify(bookRepository, times(1)).save(existingBook);
        verify(bookMapper, times(1)).toDto(bookAfterUpdate);
    }

    @Test
    @DisplayName("Delete book by id")
    void deleteById_WithExistingBook_ReturnsDeletedBook() {
        Book bookToDelete = TestUtil.createTestBook();
        bookToDelete.setId(TEST_BOOK_ID);
        BookDto expectedDto = TestUtil.createTestBookDto();

        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(bookToDelete));
        when(bookRepository.save(bookToDelete)).thenReturn(bookToDelete);
        when(bookMapper.toDto(bookToDelete)).thenReturn(expectedDto);

        BookDto actual = bookService.deleteById(TEST_BOOK_ID);

        assertEquals(expectedDto, actual);
        verify(bookRepository, times(1)).findById(TEST_BOOK_ID);
        verify(bookRepository, times(1)).save(bookToDelete);
        verify(bookMapper, times(1)).toDto(bookToDelete);
    }
}
