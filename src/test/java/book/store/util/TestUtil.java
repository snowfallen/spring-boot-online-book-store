package book.store.util;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.CreateBookRequestDto;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.model.Book;
import book.store.model.Category;
import java.math.BigDecimal;
import java.util.Set;

public class TestUtil {
    public static final Long TEST_BOOK_ID = 1L;
    public static final Long TEST_CATEGORY_ID = 1L;
    public static final String TEST_BOOK_TITLE = "Test Book";
    public static final String TEST_BOOK_AUTHOR = "Test Author";
    public static final String TEST_BOOK_ISBN = "1234567890123"; // Valid ISBN
    public static final BigDecimal TEST_BOOK_PRICE = new BigDecimal("19.99");
    public static final String TEST_BOOK_DESCRIPTION = "Test Description";
    public static final String TEST_BOOK_COVER_IMAGE = "test.jpg";
    public static final String TEST_CATEGORY_NAME = "Test Category";
    public static final String TEST_CATEGORY_DESCRIPTION = "Test Description";

    public static Book createTestBook() {
        Book book = new Book();
        book.setTitle(TEST_BOOK_TITLE);
        book.setAuthor(TEST_BOOK_AUTHOR);
        book.setIsbn(TEST_BOOK_ISBN);
        book.setPrice(TEST_BOOK_PRICE);
        book.setDescription(TEST_BOOK_DESCRIPTION);
        book.setCoverImage(TEST_BOOK_COVER_IMAGE);

        return book;
    }

    public static CreateBookRequestDto createValidBookRequestDto() {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle(TEST_BOOK_TITLE);
        dto.setAuthor(TEST_BOOK_AUTHOR);
        dto.setIsbn(TEST_BOOK_ISBN);
        dto.setPrice(TEST_BOOK_PRICE);
        dto.setDescription(TEST_BOOK_DESCRIPTION);
        dto.setCoverImage(TEST_BOOK_COVER_IMAGE);
        dto.setCategoryIds(Set.of(TEST_CATEGORY_ID));
        return dto;
    }

    public static CreateBookRequestDto createInvalidBookRequestDtoNullTitle() {
        CreateBookRequestDto dto = createValidBookRequestDto();
        dto.setTitle(null);
        return dto;
    }

    public static CreateBookRequestDto createInvalidBookRequestDtoNegativePrice() {
        CreateBookRequestDto dto = createValidBookRequestDto();
        dto.setPrice(BigDecimal.valueOf(-10.00));
        return dto;
    }

    public static CreateBookRequestDto createInvalidBookRequestDtoInvalidIsbn() {
        CreateBookRequestDto dto = createValidBookRequestDto();
        dto.setIsbn("INVALID-ISBN-123");
        return dto;
    }

    public static BookDto createTestBookDto() {
        BookDto dto = new BookDto();
        dto.setId(TEST_BOOK_ID);
        dto.setTitle(TEST_BOOK_TITLE);
        dto.setAuthor(TEST_BOOK_AUTHOR);
        dto.setIsbn(TEST_BOOK_ISBN);
        dto.setPrice(TEST_BOOK_PRICE);
        dto.setDescription(TEST_BOOK_DESCRIPTION);
        dto.setCoverImage(TEST_BOOK_COVER_IMAGE);
        dto.setCategoryIds(Set.of(TEST_CATEGORY_ID));
        return dto;
    }

    public static BookDtoWithoutCategoryIds createTestBookDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(TEST_BOOK_ID);
        dto.setTitle(TEST_BOOK_TITLE);
        dto.setAuthor(TEST_BOOK_AUTHOR);
        dto.setIsbn(TEST_BOOK_ISBN);
        dto.setPrice(TEST_BOOK_PRICE);
        dto.setDescription(TEST_BOOK_DESCRIPTION);
        dto.setCoverImage(TEST_BOOK_COVER_IMAGE);
        return dto;
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setName(TEST_CATEGORY_NAME);
        category.setDescription(TEST_CATEGORY_DESCRIPTION);
        return category;
    }

    public static CreateCategoryRequestDto createValidCategoryRequestDto() {
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
        dto.setName(TEST_CATEGORY_NAME);
        dto.setDescription(TEST_CATEGORY_DESCRIPTION);
        return dto;
    }

    public static CreateCategoryRequestDto createInvalidCategoryRequestDtoNullName() {
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
        dto.setName(null);
        dto.setDescription(TEST_CATEGORY_DESCRIPTION);
        return dto;
    }

    public static CreateCategoryRequestDto createInvalidCategoryRequestDtoEmptyName() {
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
        dto.setName("");
        dto.setDescription(TEST_CATEGORY_DESCRIPTION);
        return dto;
    }

    public static CategoryDto createTestCategoryDto() {
        return new CategoryDto(TEST_CATEGORY_ID.intValue(),
                TEST_CATEGORY_NAME, TEST_CATEGORY_DESCRIPTION);
    }
}