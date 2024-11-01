package book.store.service.shopping.cart;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartResponseDto addCartItem(CartItemRequestDto requestDto);

    ShoppingCartResponseDto get();

    ShoppingCartResponseDto update(CartItemUpdateQuantityDto updateQuantityDto, Long id);

    void delete(Long id);
}
