package book.store.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.config.TestConfig;
import book.store.model.Book;
import book.store.model.Category;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest extends TestConfig {
    private static final Long TEST_CATEGORY_ID = 1L;
    private static final int EXPECTED_BOOKS_WITH_CATEGORY = 3;
    private static final int PAGE_SIZE = 10;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");
        entityManager.persist(category);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890123");
        book.setPrice(java.math.BigDecimal.valueOf(19.99));

        Set<Category> categories = new HashSet<>();
        categories.add(category);
        book.setCategories(categories);

        entityManager.persist(book);
        entityManager.flush();
    }

    @Test
    @DisplayName("Find all books by category when category exists")
    void findAllBooksByCategoryId_WithExistingCategory_ReturnsBooks() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);

        List<Book> actual = bookRepository.findAllBooksByCategoryId(TEST_CATEGORY_ID, pageable);

        assertNotNull(actual);
        assertEquals(EXPECTED_BOOKS_WITH_CATEGORY, actual.size());
        actual.forEach(book -> assertTrue(
                book.getCategories().stream()
                        .anyMatch(category -> category.getId().equals(TEST_CATEGORY_ID))
        ));
    }
}
