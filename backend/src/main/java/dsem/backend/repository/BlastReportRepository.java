package dsem.backend.repository;

import dsem.backend.model.entity.BlastReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlastReportRepository extends JpaRepository<BlastReport, Long> {
    Optional<BlastReport> findByDispatchId(Long dispatchId);
    boolean existsByDispatchId(Long dispatchId);
    List<BlastReport> findByBlastTimeBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(b.quantityUsed), 0) FROM BlastReport b " +
           "WHERE b.blastTime BETWEEN :from AND :to")
    Double sumQuantityUsedBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(b.quantityUsed), 0) FROM BlastReport b " +
           "WHERE b.dispatch.tunnelRequest.explosive.id = :explosiveId " +
           "AND b.blastTime BETWEEN :from AND :to")
    Double sumQuantityUsedByExplosiveBetween(
            @Param("explosiveId") Long explosiveId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
