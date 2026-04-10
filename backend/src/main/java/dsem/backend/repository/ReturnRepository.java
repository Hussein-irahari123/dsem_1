package dsem.backend.repository;

import dsem.backend.model.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ReturnRepository extends JpaRepository<Return, Long> {
    Optional<Return> findByBlastReportId(Long blastReportId);
    boolean existsByBlastReportId(Long blastReportId);

    @Query("SELECT COALESCE(SUM(r.quantityReturned), 0) FROM Return r " +
           "WHERE r.returnedAt BETWEEN :from AND :to")
    Double sumReturnedQuantityBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(r.quantityReturned), 0) FROM Return r " +
           "WHERE r.blastReport.dispatch.tunnelRequest.explosive.id = :explosiveId " +
           "AND r.returnedAt BETWEEN :from AND :to")
    Double sumReturnedQuantityByExplosiveBetween(
            @Param("explosiveId") Long explosiveId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
