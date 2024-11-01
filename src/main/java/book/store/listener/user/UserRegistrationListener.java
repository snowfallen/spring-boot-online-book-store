package book.store.listener.user;

import book.store.event.user.UserRegisteredEvent;
import book.store.service.shopping.cart.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationListener {
    private final ShoppingCartService shoppingCartService;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        shoppingCartService.createShoppingCart(event.getUser());
    }
}
