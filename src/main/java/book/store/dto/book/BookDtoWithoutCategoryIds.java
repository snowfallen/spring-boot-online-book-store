package book.store.dto.book;

import java.math.BigDecimal;

public record BookDtoWithoutCategoryIds(Long id, String string, String author,
                                        String isbn, BigDecimal price,
                                        String description, String coverImage) {
}
