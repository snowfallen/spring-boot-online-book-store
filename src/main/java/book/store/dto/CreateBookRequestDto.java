package book.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateBookRequestDto {
    @NotBlank
    @Length(min = 2)
    private String title;
    @NotBlank
    @Length(min = 2)
    private String author;
    @NotBlank
    @Length(min = 13, max = 13)
    private String isbn;
    @Positive
    @NotNull
    private BigDecimal price;
    private String description;
    private String coverImage;
}
