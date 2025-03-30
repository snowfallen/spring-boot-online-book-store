package book.store.repository.order;

import book.store.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId, Pageable pageable);
    
    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);
} 
