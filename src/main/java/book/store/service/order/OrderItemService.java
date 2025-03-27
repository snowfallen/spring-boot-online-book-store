package book.store.service.order;

import book.store.dto.order.OrderItemResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {
    List<OrderItemResponseDto> getOrderItems(Long orderId, Pageable pageable);
    
    OrderItemResponseDto getOrderItem(Long orderId, Long itemId);
} 
