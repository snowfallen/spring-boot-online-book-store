package book.store.controller;

import book.store.dto.order.OrderItemResponseDto;
import book.store.service.order.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order Item management", description = "Endpoints for managing order items")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders/{orderId}/items")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Operation(summary = "Get all items in an order",
            description = "Retrieve all items for a specific order")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<OrderItemResponseDto> getOrderItems(
            @PathVariable Long orderId,
            Pageable pageable
    ) {
        return orderItemService.getOrderItems(orderId, pageable);
    }

    @Operation(summary = "Get specific item in an order",
            description = "Retrieve specific item from an order by its ID")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{itemId}")
    public OrderItemResponseDto getOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        return orderItemService.getOrderItem(orderId, itemId);
    }
}
