package dsem.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "returns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "blast_report_id", nullable = false, unique = true)
    private BlastReport blastReport;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "received_by_id", nullable = false)
    private User receivedBy;

    @Column(nullable = false)
    private Double quantityReturned;

    @Column(updatable = false)
    private LocalDateTime returnedAt;

    @PrePersist
    protected void onCreate() {
        this.returnedAt = LocalDateTime.now();
    }
}
