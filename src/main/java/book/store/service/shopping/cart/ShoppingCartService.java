package book.store.service.shopping.cart;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartResponseDto addCartItem(CartItemRequestDto requestDto,
                                        Authentication authentication);

    ShoppingCartResponseDto get(Authentication authentication);

    ShoppingCartResponseDto update(
            CartItemUpdateQuantityDto updateQuantityDto,
            Long id, Authentication authentication);

    void delete(Long id, Authentication authentication);
}
