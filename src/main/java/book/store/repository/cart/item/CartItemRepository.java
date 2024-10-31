package book.store.repository.cart.item;

import book.store.model.CartItem;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Set<CartItem> findCartItemsByShoppingCartId(@Param("shoppingCartId") Long shoppingCart);
}
