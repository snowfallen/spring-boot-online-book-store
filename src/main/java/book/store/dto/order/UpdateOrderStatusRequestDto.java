package book.store.dto.order;

import book.store.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequestDto {
    @NotNull
    private Order.Status status;
} 
