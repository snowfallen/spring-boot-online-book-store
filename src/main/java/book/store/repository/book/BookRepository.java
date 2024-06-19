package book.store.repository.book;

import book.store.model.Book;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("FROM Book b INNER JOIN FETCH b.categories c WHERE c.id = :categoryId")
    List<Book> findAllBooksByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
}
