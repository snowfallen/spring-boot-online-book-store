package book.store.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.config.TestConfig;
import book.store.model.Book;
import book.store.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/database/repository/add-category-and-three-books.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(scripts = "/database/repository/clean-up-data.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class BookRepositoryTest extends TestConfig {
    private static final int EXPECTED_BOOKS_WITH_CATEGORY = 3;
    private static final int PAGE_SIZE = 10;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by category when category exists")
    void findAllBooksByCategoryId_WithExistingCategory_ReturnsBooks() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);

        List<Book> actual = bookRepository.findAllBooksByCategoryId(
                TestUtil.TEST_CATEGORY_ID, pageable
        );

        assertNotNull(actual);
        assertEquals(EXPECTED_BOOKS_WITH_CATEGORY, actual.size());
        actual.forEach(book -> assertTrue(
                book.getCategories().stream()
                        .anyMatch(category -> category.getId()
                                .equals(TestUtil.TEST_CATEGORY_ID)
                        )
        ));
    }
}
