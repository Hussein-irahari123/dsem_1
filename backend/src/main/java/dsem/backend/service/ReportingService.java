package dsem.backend.service;

import dsem.backend.model.entity.Inventory;
import dsem.backend.model.enums.MagazineType;
import dsem.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final InventoryRepository inventoryRepository;
    private final BlastReportRepository blastReportRepository;
    private final DispatchRepository dispatchRepository;
    private final ReturnRepository returnRepository;
    private final ExplosiveRepository explosiveRepository;

    /** Current stock snapshot across all magazines */
    public Map<String, Object> getStockReport() {
        List<Inventory> mainStock = inventoryRepository.findByMagazineType(MagazineType.MAIN);
        List<Inventory> subStock  = inventoryRepository.findByMagazineType(MagazineType.SUB);
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("mainMagazine", mainStock);
        report.put("subMagazine", subStock);
        return report;
    }

    /** Total quantity used in blasts for a given day */
    public Map<String, Object> getDailyUsageReport(LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to   = date.atTime(LocalTime.MAX);
        Double totalUsed   = blastReportRepository.sumQuantityUsedBetween(from, to);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("date", date.toString());
        report.put("totalQuantityUsed", totalUsed);
        report.put("blastReports", blastReportRepository.findByBlastTimeBetween(from, to));
        return report;
    }

    /**
     * Blast efficiency = (quantityUsed / quantityDispatched) × 100
     * Loss = dispatched − (used + returned)
     */
    public Map<String, Object> getBlastEfficiencyReport(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.atTime(LocalTime.MAX);

        double totalDispatched = dispatchRepository.findByDispatchTimeBetween(fromDt, toDt)
                .stream().mapToDouble(d -> d.getQuantityDispatched()).sum();
        double totalUsed       = blastReportRepository.sumQuantityUsedBetween(fromDt, toDt);
        double efficiency      = totalDispatched > 0 ? (totalUsed / totalDispatched) * 100 : 0;

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("from", from.toString());
        report.put("to", to.toString());
        report.put("totalDispatched", totalDispatched);
        report.put("totalUsed", totalUsed);
        report.put("blastEfficiencyPercent", Math.round(efficiency * 100.0) / 100.0);
        return report;
    }

    /**
     * Loss = dispatched − (used + returned)   [should be 0 for perfect traceability]
     */
    public Map<String, Object> getLossReport(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.atTime(LocalTime.MAX);

        double totalDispatched = dispatchRepository.findByDispatchTimeBetween(fromDt, toDt)
                .stream().mapToDouble(d -> d.getQuantityDispatched()).sum();
        double totalUsed     = blastReportRepository.sumQuantityUsedBetween(fromDt, toDt);
        double totalReturned = returnRepository.sumReturnedQuantityBetween(fromDt, toDt);
        double loss          = totalDispatched - (totalUsed + totalReturned);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("from", from.toString());
        report.put("to", to.toString());
        report.put("totalDispatched", totalDispatched);
        report.put("totalUsed", totalUsed);
        report.put("totalReturned", totalReturned);
        report.put("unaccountedLoss", Math.max(0, loss));
        report.put("lossDetected", loss > 0);
        return report;
    }
}
