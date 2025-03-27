package book.store.dto.cart.item;

import jakarta.validation.constraints.Positive;

public record CartItemUpdateQuantityDto(
        @Positive
        int quantity
) {
}
