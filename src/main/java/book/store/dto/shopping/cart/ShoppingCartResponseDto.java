package book.store.dto.shopping.cart;

import book.store.dto.cart.item.CartItemResponseDto;
import java.util.Set;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        Set<CartItemResponseDto> cartItems
) {
}
