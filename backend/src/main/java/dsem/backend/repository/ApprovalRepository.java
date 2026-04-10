package dsem.backend.repository;

import dsem.backend.model.entity.Approval;
import dsem.backend.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    Optional<Approval> findByTunnelRequestId(Long tunnelRequestId);
    List<Approval> findByStatus(RequestStatus status);
}
