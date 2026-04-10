package dsem.backend.model.entity;

import dsem.backend.model.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tunnel_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TunnelRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "explosive_id", nullable = false)
    private Explosive explosive;

    @Column(nullable = false, length = 100)
    private String tunnelLocation;

    @Column(nullable = false, length = 200)
    private String purpose;

    @Column(nullable = false)
    private Double quantityRequested;

    @Builder.Default
    private Double quantityReturned = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(updatable = false)
    private LocalDateTime requestDate;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        this.requestDate = LocalDateTime.now();
    }
}
