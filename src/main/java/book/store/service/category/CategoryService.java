package book.store.service.category;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto categoryRequestDto);

    CategoryDto update(Long id, CreateCategoryRequestDto categoryRequestDto);

    CategoryDto deleteById(Long id);
}
