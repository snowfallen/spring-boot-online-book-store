package book.store.controller;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Create a new book", description = "Create a new book")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @Operation(summary = "Get book by id",
            description = "Get book details by its unique identifier (ID)")
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(summary = "Get all books",
            description = "Retrieves a paginated list of all available books")
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @Operation(summary = "Update book by id",
            description = "Updates an existing book by id")
    @PutMapping("/{id}")
    public BookDto updateById(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.updateById(id, bookDto);
    }

    @Operation(summary = "Delete book by id",
            description = "Soft deletes a book by its unique identifier (ID)")
    @DeleteMapping("/{id}")
    public BookDto deleteById(@PathVariable Long id) {
        return bookService.deleteById(id);
    }

    @Operation(summary = "Search book", description = "Search book by title or author")
    @GetMapping("/search")
    public List<BookDto> search(BookSearchParametersDto bookSearchParametersDto,
                                Pageable pageable) {
        return bookService.search(bookSearchParametersDto, pageable);
    }
}
