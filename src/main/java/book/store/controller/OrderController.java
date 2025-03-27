package book.store.controller;

import book.store.dto.order.OrderRequestDto;
import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.UpdateOrderStatusRequestDto;
import book.store.model.User;
import book.store.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Place a new order",
            description = "Place a new order based on user's shopping cart")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public OrderResponseDto createOrder(
            Authentication authentication,
            @RequestBody @Valid OrderRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(requestDto, user.getId());
    }

    @Operation(summary = "Get user's order history",
            description = "Retrieve all orders for authenticated user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<OrderResponseDto> getOrders(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrders(user.getId(), pageable);
    }

    @Operation(summary = "Update order status",
            description = "Update status of specific order (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public OrderResponseDto updateOrderStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusRequestDto requestDto
    ) {
        return orderService.updateOrderStatus(id, requestDto);
    }
} 
