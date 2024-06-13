package book.store.repository.book;

import book.store.dto.book.BookSearchParametersDto;
import book.store.model.Book;
import book.store.repository.SpecificationBuilder;
import book.store.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements
        SpecificationBuilder<Book, BookSearchParametersDto> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);

        String[] authors = searchParametersDto.authors();
        if (authors != null && authors.length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("author").getSpecification(authors));
        }

        String[] titles = searchParametersDto.titles();
        if (titles != null && titles.length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("title").getSpecification(titles));
        }

        return specification;
    }
}
