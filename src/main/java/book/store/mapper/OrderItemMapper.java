package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.order.OrderItemResponseDto;
import book.store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemResponseDto toDto(OrderItem orderItem);
} 
