package dsem.backend.controller;

import dsem.backend.model.entity.AuditLog;
import dsem.backend.service.AuditService;
import dsem.backend.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportingService reportingService;
    private final AuditService auditService;

    @GetMapping("/api/reports/stock")
    public ResponseEntity<Map<String, Object>> getStockReport() {
        return ResponseEntity.ok(reportingService.getStockReport());
    }

    @GetMapping("/api/reports/daily-usage")
    public ResponseEntity<Map<String, Object>> getDailyUsage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(reportingService.getDailyUsageReport(target));
    }

    @GetMapping("/api/reports/blast-efficiency")
    public ResponseEntity<Map<String, Object>> getBlastEfficiency(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportingService.getBlastEfficiencyReport(
                from != null ? from : LocalDate.now().minusDays(30),
                to != null ? to : LocalDate.now()));
    }

    @GetMapping("/api/reports/losses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLossReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportingService.getLossReport(
                from != null ? from : LocalDate.now().minusDays(30),
                to != null ? to : LocalDate.now()));
    }

    @GetMapping("/api/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }
}
