package dsem.backend.repository;

import dsem.backend.model.entity.TunnelRequest;
import dsem.backend.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface TunnelRequestRepository extends JpaRepository<TunnelRequest, Long> {
    List<TunnelRequest> findByStatus(RequestStatus status);
    List<TunnelRequest> findByRequestedById(Long userId);
    List<TunnelRequest> findByExplosiveId(Long explosiveId);

    @Query("SELECT COALESCE(SUM(r.quantityRequested), 0) FROM TunnelRequest r " +
           "WHERE r.status IN ('APPROVED', 'DISPATCHED', 'COMPLETED') " +
           "AND r.requestDate BETWEEN :from AND :to")
    Double sumApprovedQuantityBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(r.quantityRequested), 0) FROM TunnelRequest r " +
           "WHERE r.explosive.id = :explosiveId " +
           "AND r.status IN ('APPROVED', 'DISPATCHED', 'COMPLETED') " +
           "AND r.requestDate BETWEEN :from AND :to")
    Double sumApprovedQuantityByExplosiveBetween(
            @Param("explosiveId") Long explosiveId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
