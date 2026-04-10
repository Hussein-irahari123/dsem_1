package dsem.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blast_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlastReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dispatch_id", nullable = false, unique = true)
    private Dispatch dispatch;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "recorded_by_id", nullable = false)
    private User recordedBy;

    @Column(nullable = false)
    private Integer holesDrilled;

    @Column(nullable = false)
    private Double quantityUsed;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(updatable = false)
    private LocalDateTime blastTime;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        this.blastTime = LocalDateTime.now();
    }
}
