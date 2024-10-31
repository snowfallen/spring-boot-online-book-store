package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.cart.item.CartItemResponseDto;
import book.store.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItemResponseDto toDto(CartItem cartItem);

    @AfterMapping
    default void setBookId(@MappingTarget CartItemResponseDto cartItemResponseDto,
                           CartItem cartItem) {
        cartItemResponseDto.setBookId(cartItem.getBook().getId());
    }

    @AfterMapping
    default void setBookTitle(@MappingTarget CartItemResponseDto cartItemResponseDto,
                              CartItem cartItem) {
        cartItemResponseDto.setBookTitle(String.valueOf(cartItem.getBook().getTitle()));
    }
}
