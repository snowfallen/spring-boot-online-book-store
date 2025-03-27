package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "cartItems", source = "cartItems", qualifiedByName = "toDtoSet")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setUserId(@MappingTarget ShoppingCartResponseDto responseDto,
                           ShoppingCart shoppingCart) {
        responseDto.setUserId(shoppingCart.getUser().getId());
    }
}
