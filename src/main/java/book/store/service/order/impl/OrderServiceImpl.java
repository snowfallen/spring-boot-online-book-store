package book.store.service.order.impl;

import book.store.dto.order.OrderItemResponseDto;
import book.store.dto.order.OrderRequestDto;
import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.UpdateOrderStatusRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.exception.OrderProcessingException;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.model.CartItem;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import book.store.repository.order.OrderItemRepository;
import book.store.repository.order.OrderRepository;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import book.store.service.order.OrderService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final String CAN_T_FIND_ORDER_ITEM_WITH_ID
            = "Can't find order item with id: ";
    private static final String CAN_T_FIND_SHOPPING_CART_FOR_USER
            = "Can't find shopping cart for user: ";
    private static final String CAN_T_FIND_ORDER_BY_ID
            = "Can't find order by id: ";
    private static final String EMPTY_SHOPPING_CART
            = "Cannot create order with empty shopping cart";

    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException(CAN_T_FIND_SHOPPING_CART_FOR_USER + userId));
        
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProcessingException(EMPTY_SHOPPING_CART);
        }
        
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setOrderItems(getOrderItemsFromCart(order, shoppingCart.getCartItems()));
        order.setTotal(calculateTotal(order.getOrderItems()));
        
        orderRepository.save(order);
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrders(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long id, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CAN_T_FIND_ORDER_BY_ID + id));
        order.setStatus(requestDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemResponseDto> getOrderItems(Long orderId, Pageable pageable) {
        return orderItemRepository.findAllByOrderId(orderId, pageable)
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItem(Long orderId, Long itemId) {
        return orderItemMapper.toDto(orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        CAN_T_FIND_ORDER_ITEM_WITH_ID + itemId)));
    }

    private Set<OrderItem> getOrderItemsFromCart(Order order, Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    return orderItem;
                })
                .collect(Collectors.toSet());
    }

    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
