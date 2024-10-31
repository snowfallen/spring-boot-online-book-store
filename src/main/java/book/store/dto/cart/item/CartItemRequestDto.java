package book.store.dto.cart.item;

import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @Positive
        Long bookId,
        @Positive
        int quantity
) {
}
