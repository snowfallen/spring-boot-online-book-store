package book.store.service.cart.item.impl;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemResponseDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.repository.cart.item.CartItemRepository;
import book.store.service.book.BookService;
import book.store.service.cart.item.CartItemService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private static final String CAN_T_FIND_CART_BY_ID = "Can`t find cart by ID : ";

    private final CartItemRepository cartItemRepository;
    private final BookService bookService;
    private final CartItemMapper mapper;

    @Override
    @Transactional
    public CartItem createCartItem(CartItemRequestDto cartItemRequestDto,
                                   ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        Long bookId = cartItemRequestDto.bookId();
        cartItem.setBook(bookService.getBookById(bookId));
        cartItem.setQuantity(cartItemRequestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);

        return cartItemRepository.save(cartItem);
    }

    @Override
    public Set<CartItemResponseDto> findByShoppingCartId(Long id) {
        return cartItemRepository.findCartItemsByShoppingCartId(id).stream()
                .map(mapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CartItemResponseDto update(CartItemUpdateQuantityDto updateQuantityDto, Long id) {
        CartItem cartItem = getCartItem(id);
        cartItem.setQuantity(updateQuantityDto.quantity());
        return mapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemResponseDto delete(Long id) {
        CartItemResponseDto deletedCartItem = mapper.toDto(getCartItem(id));
        cartItemRepository.deleteById(deletedCartItem.getId());
        return deletedCartItem;
    }

    private CartItem getCartItem(Long id) {
        return cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(CAN_T_FIND_CART_BY_ID + id)
        );
    }
}
