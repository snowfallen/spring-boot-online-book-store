package book.store.service.book;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    BookDto getBookDtoById(Long id);

    List<BookDto> getAll(Pageable pageable);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    BookDto deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto, Pageable pageable);

    List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id, Pageable pageable);
}
