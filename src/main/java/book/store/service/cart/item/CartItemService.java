package book.store.service.cart.item;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemResponseDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import java.util.Set;

public interface CartItemService {
    CartItem createCartItem(CartItemRequestDto cartItemRequestDto,
                            ShoppingCart shoppingCart);

    Set<CartItemResponseDto> findByShoppingCartId(Long id);

    CartItemResponseDto update(CartItemUpdateQuantityDto updateQuantityDto, Long id);

    CartItemResponseDto delete(Long cartItemId);
}
