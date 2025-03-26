package book.store.service.shopping.cart.impl;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.cart.item.CartItemRepository;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import book.store.service.book.BookService;
import book.store.service.shopping.cart.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final String CAN_T_FIND_SHOPPING_CART = "Can't find shopping cart!";
    private static final String CAN_T_FIND_CART_BY_ID = "Can`t find cart by ID : ";

    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookService bookService;
    private final BookMapper bookMapper;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto addCartItem(CartItemRequestDto requestDto, Long userId) {
        String requestBookIsbn = bookService.getBookDtoById(requestDto.bookId()).getIsbn();
        ShoppingCart shoppingCart = getShoppingCart(userId);

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
    public ShoppingCartResponseDto get(Long userId) {
        return shoppingCartMapper.toDto(getShoppingCart(userId));
    }

    @Override
    public ShoppingCartResponseDto update(CartItemUpdateQuantityDto updateQuantityDto, 
                                          Long id, Long userId) {
        CartItem cartItem = getCartItem(id, userId);
        cartItem.setQuantity(updateQuantityDto.quantity());
        return shoppingCartMapper.toDto(cartItemRepository.save(cartItem).getShoppingCart());
    }

    @Override
    public void delete(Long id, Long userId) {
        CartItem cartItem = getCartItem(id, userId);
        cartItem.getShoppingCart().getCartItems().remove(cartItem);
        cartItemRepository.deleteById(cartItem.getId());
    }

    private CartItem createCartItem(CartItemRequestDto cartItemRequestDto,
                                   ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        Long bookId = cartItemRequestDto.bookId();
        cartItem.setBook(bookMapper.toModel(bookService.getBookDtoById(bookId)));
        cartItem.setQuantity(cartItemRequestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);

        return cartItem;
    }

    private CartItem getCartItem(Long id, Long userId) {
        return cartItemRepository.findByIdAndShoppingCartId(id, getShoppingCart(userId).getId())
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_CART_BY_ID + id)
        );
    }

    private ShoppingCart getShoppingCart(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_SHOPPING_CART));
    }
}
