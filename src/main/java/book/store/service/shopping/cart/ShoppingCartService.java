package book.store.service.shopping.cart;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartResponseDto addCartItem(CartItemRequestDto requestDto, Long userId);

    ShoppingCartResponseDto get(Long userId);

    ShoppingCartResponseDto update(
            CartItemUpdateQuantityDto updateQuantityDto,
            Long id, Long userId);

    void delete(Long id, Long userId);
}
