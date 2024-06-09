package book.store.service.impl;

import book.store.dto.BookDto;
import book.store.dto.BookSearchParametersDto;
import book.store.dto.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import book.store.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private static final String BOOK_NOT_FOUND = "Book not found by id: ";
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookMapper.toDto(getBook(id));
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookMapper.toDtoList(bookRepository.findAll(pageable));
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto bookDto) {
        Book bookToUpdate = getBook(id);
        bookToUpdate.setTitle(bookDto.getTitle());
        bookToUpdate.setAuthor(bookDto.getAuthor());
        bookToUpdate.setIsbn(bookDto.getIsbn());
        bookToUpdate.setDescription(bookDto.getDescription());
        bookToUpdate.setCoverImage(bookDto.getCoverImage());

        return bookMapper.toDto(bookRepository.save(bookToUpdate));
    }

    @Override
    public BookDto deleteById(Long id) {
        Book bookToDelete = getBook(id);
        bookToDelete.setDeleted(true);
        return bookMapper.toDto(bookRepository.save(bookToDelete));
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto params, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookMapper.toDtoList(bookRepository.findAll(bookSpecification, pageable));
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(BOOK_NOT_FOUND + id)
        );
    }
}
