package dsem.backend.model.entity;

import dsem.backend.model.enums.MagazineType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"explosive_id", "magazine_type"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "explosive_id", nullable = false)
    private Explosive explosive;

    @Enumerated(EnumType.STRING)
    @Column(name = "magazine_type", nullable = false)
    private MagazineType magazineType;

    @Column(nullable = false)
    @Builder.Default
    private Double quantityInStock = 0.0;

    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
