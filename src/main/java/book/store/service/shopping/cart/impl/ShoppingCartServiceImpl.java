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
import book.store.service.user.UserService;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final String CAN_T_FIND_SHOPPING_CART = "Can't find shopping cart!";
    private static final String CAN_T_FIND_CART_BY_ID = "Can`t find cart by ID : ";

    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookService bookService;
    private final UserService userService;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto addCartItem(CartItemRequestDto requestDto) {
        String requestBookIsbn = bookService.getBookById(requestDto.bookId()).getIsbn();
        ShoppingCart shoppingCart = getShoppingCart();

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
    public ShoppingCartResponseDto get() {
        return shoppingCartMapper.toDto(getShoppingCart());
    }

    @Override
    public ShoppingCartResponseDto update(CartItemUpdateQuantityDto updateQuantityDto, Long id) {
        CartItem cartItem = getCartItem(id);
        cartItem.setQuantity(updateQuantityDto.quantity());
        return shoppingCartMapper.toDto(cartItemRepository.save(cartItem).getShoppingCart());
    }

    @Override
    public void delete(Long id) {
        cartItemRepository.deleteById(getCartItem(id).getId());
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

    private ShoppingCart getShoppingCart() {
        return shoppingCartRepository.findByUserId(getAuthUserId())
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_SHOPPING_CART));
    }

    private Long getAuthUserId() {
        return userService.getAuthUser().getId();
    }
}
