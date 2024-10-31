package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);
}
