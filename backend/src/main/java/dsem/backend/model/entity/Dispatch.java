package dsem.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tunnel_request_id", nullable = false, unique = true)
    private TunnelRequest tunnelRequest;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "issued_by_id", nullable = false)
    private User issuedBy;

    @Column(nullable = false, length = 50)
    private String vehicleNumber;

    @Column(nullable = false, length = 100)
    private String driverName;

    @Column(nullable = false)
    private Double quantityDispatched;

    @Column(updatable = false)
    private LocalDateTime dispatchTime;

    @PrePersist
    protected void onCreate() {
        this.dispatchTime = LocalDateTime.now();
    }
}
