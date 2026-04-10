package dsem.backend.model.entity;

import dsem.backend.model.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tunnel_request_id", nullable = false, unique = true)
    private TunnelRequest tunnelRequest;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "approved_by_id", nullable = false)
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status; // APPROVED or REJECTED

    @Column(length = 500)
    private String comment;

    private LocalDateTime approvedAt;

    @PrePersist
    protected void onApprove() {
        this.approvedAt = LocalDateTime.now();
    }
}
