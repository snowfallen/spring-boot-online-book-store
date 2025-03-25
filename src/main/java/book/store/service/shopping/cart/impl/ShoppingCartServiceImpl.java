package book.store.service.shopping.cart.impl;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.cart.item.CartItemRepository;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import book.store.service.book.BookService;
import book.store.service.shopping.cart.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final String CAN_T_FIND_SHOPPING_CART = "Can't find shopping cart!";
    private static final String CAN_T_FIND_CART_BY_ID = "Can`t find cart by ID : ";
    private static final String CART_ITEM_ACCESS_DENIED =
            "Cart item with id %d does not belong to the current user";

    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookService bookService;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto addCartItem(CartItemRequestDto requestDto,
                                               Authentication authentication) {
        String requestBookIsbn = bookService.getBookDtoById(requestDto.bookId()).getIsbn();
        ShoppingCart shoppingCart = getShoppingCart(authentication);

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(i -> i.getBook().getIsbn().equals(requestBookIsbn))
                .findFirst()
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + requestDto.quantity());
                    return existingItem;
                })
                .orElseGet(() -> {
                    CartItem newCartItem = createCartItem(requestDto, shoppingCart);
                    shoppingCart.getCartItems().add(newCartItem);
                    return newCartItem;
                });
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto get(Authentication authentication) {
        return shoppingCartMapper.toDto(getShoppingCart(authentication));
    }

    @Override
    public ShoppingCartResponseDto update(CartItemUpdateQuantityDto updateQuantityDto, 
                                          Long id, Authentication authentication) {
        CartItem cartItem = getCartItemForUser(id, authentication);
        cartItem.setQuantity(updateQuantityDto.quantity());
        return shoppingCartMapper.toDto(cartItemRepository.save(cartItem).getShoppingCart());
    }

    @Override
    public void delete(Long id, Authentication authentication) {
        CartItem cartItemForUser = getCartItemForUser(id, authentication);
        cartItemForUser.getShoppingCart().getCartItems().remove(cartItemForUser);
        cartItemRepository.deleteById(cartItemForUser.getId());
    }

    private CartItem createCartItem(CartItemRequestDto cartItemRequestDto,
                                   ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        Long bookId = cartItemRequestDto.bookId();
        cartItem.setBook(bookService.getBookById(bookId));
        cartItem.setQuantity(cartItemRequestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);

        return cartItem;
    }

    private CartItem getCartItem(Long id) {
        return cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(CAN_T_FIND_CART_BY_ID + id)
        );
    }

    private CartItem getCartItemForUser(Long cartItemId, Authentication authentication) {
        ShoppingCart shoppingCart = getShoppingCart(authentication);
        CartItem cartItem = getCartItem(cartItemId);

        if (!shoppingCart.getCartItems().contains(cartItem)) {
            throw new EntityNotFoundException(String.format(CART_ITEM_ACCESS_DENIED, cartItemId));
        }

        return cartItem;
    }

    private ShoppingCart getShoppingCart(Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        return shoppingCartRepository.findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_SHOPPING_CART));
    }
}
