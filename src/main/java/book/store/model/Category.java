package book.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Setter
@Getter
@SQLDelete(sql = "UPDATE categories WHERE id = ? SET is_deleted = true")
@Where(clause = "is_deleted = false")
@Table(name = "categories")
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @Column(name = "is_deleted", nullable = false, columnDefinition = "TINYINT")
    private boolean isDeleted = false;

    public Category(Long id) {
        this.id = id;
    }
}
