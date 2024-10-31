package book.store.service.shopping.cart.impl;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemResponseDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import book.store.service.cart.item.CartItemService;
import book.store.service.shopping.cart.ShoppingCartService;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final String CAN_T_FIND_SHOPPING_CART = "Can't find shopping cart!";

    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public void register(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCartRepository.save(shoppingCart);

    }

    @Override
    public CartItemResponseDto addCartItem(CartItemRequestDto requestDto,
                                           Authentication authentication) {
        ShoppingCart shoppingCart = getShoppingCart((User) authentication.getPrincipal());
        CartItem cartItem = cartItemService.createCartItem(requestDto, shoppingCart);
        shoppingCart.getCartItems().add(cartItem);

        return cartItemMapper.toDto(cartItem);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto get(Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        ShoppingCart shoppingCart = getShoppingCart(authenticatedUser);
        Long id = shoppingCart.getId();

        return new ShoppingCartResponseDto(
                id, authenticatedUser.getId(), cartItemService.findByShoppingCartId(id)
        );
    }

    private ShoppingCart getShoppingCart(User authenticatedUser) {
        return shoppingCartRepository.findWithCartItemsById(authenticatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_SHOPPING_CART));
    }
}
