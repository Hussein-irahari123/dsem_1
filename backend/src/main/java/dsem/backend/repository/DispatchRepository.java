package dsem.backend.repository;

import dsem.backend.model.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
    Optional<Dispatch> findByTunnelRequestId(Long tunnelRequestId);
    boolean existsByTunnelRequestId(Long tunnelRequestId);
    List<Dispatch> findByDispatchTimeBetween(LocalDateTime from, LocalDateTime to);
}
