package book.store.service.shopping.cart;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemResponseDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    void register(User user);

    CartItemResponseDto addCartItem(CartItemRequestDto requestDto,
                                    Authentication authentication);

    ShoppingCartResponseDto get(Authentication authentication);
}
