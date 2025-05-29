package book.store.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class CreateCategoryRequestDto {
    @NotBlank
    @Length(min = 3, max = 255)
    private String name;
    @Length(max = 255)
    private String description;
}
