package book.store.service.order.impl;

import book.store.dto.order.OrderItemResponseDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderItemMapper;
import book.store.repository.order.OrderItemRepository;
import book.store.service.order.OrderItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private static final String CAN_T_FIND_ORDER_ITEM_WITH_ID = "Can't find order item with id: ";

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

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
} 
