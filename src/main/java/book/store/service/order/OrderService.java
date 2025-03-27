package book.store.service.order;

import book.store.dto.order.OrderRequestDto;
import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.UpdateOrderStatusRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto requestDto, Long userId);
    
    List<OrderResponseDto> getOrders(Long userId, Pageable pageable);
    
    OrderResponseDto updateOrderStatus(Long id, UpdateOrderStatusRequestDto requestDto);
} 
