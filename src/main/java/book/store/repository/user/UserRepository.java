package book.store.repository.user;

import book.store.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query("FROM User u LEFT JOIN FETCH u.roles r "
            + "WHERE u.email = :email "
            + "AND r.isDeleted = FALSE")
    Optional<User> findUserByEmail(String email);
}
