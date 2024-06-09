package book.store.service;

import book.store.dto.BookDto;
import book.store.dto.BookSearchParametersDto;
import book.store.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    BookDto getBookById(Long id);

    List<BookDto> getAll(Pageable pageable);

    BookDto updateById(Long id, CreateBookRequestDto bookDto);

    BookDto deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto, Pageable pageable);
}
