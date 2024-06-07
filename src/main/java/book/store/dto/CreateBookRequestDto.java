package book.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateBookRequestDto {
    @NotNull
    @Length(min = 2)
    private String title;
    @NotNull
    @Length(min = 2)
    private String author;
    @NotNull
    @Length(min = 13, max = 13)
    private String isbn;
    @NotNull
    @Min(1)
    private BigDecimal price;
    private String description;
    private String coverImage;
}
