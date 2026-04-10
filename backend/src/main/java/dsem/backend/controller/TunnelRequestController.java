package dsem.backend.controller;

import dsem.backend.dto.request.DemandSummaryDTO;
import dsem.backend.model.entity.TunnelRequest;
import dsem.backend.service.DemandCalculationEngine;
import dsem.backend.service.TunnelRequestService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class TunnelRequestController {

    private final TunnelRequestService tunnelRequestService;
    private final DemandCalculationEngine demandCalculationEngine;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<List<TunnelRequest>> getAll() {
        return ResponseEntity.ok(tunnelRequestService.getAllRequests());
    }

    @GetMapping("/my")
    public ResponseEntity<List<TunnelRequest>> getMyRequests() {
        return ResponseEntity.ok(tunnelRequestService.getMyRequests());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<List<TunnelRequest>> getPending() {
        return ResponseEntity.ok(tunnelRequestService.getPendingRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TunnelRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tunnelRequestService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public ResponseEntity<TunnelRequest> create(@RequestBody CreateRequestBody body) {
        return ResponseEntity.ok(tunnelRequestService.createRequest(
                body.getExplosiveId(),
                body.getTunnelLocation(),
                body.getPurpose(),
                body.getQuantityRequested(),
                body.getNotes()
        ));
    }

    @GetMapping("/demand-summary")
    public ResponseEntity<List<DemandSummaryDTO>> getDemandSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : LocalDate.now().atStartOfDay();
        LocalDateTime toDt = (to != null) ? to.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        return ResponseEntity.ok(demandCalculationEngine.calculateDemand(fromDt, toDt));
    }

    @Data
    static class CreateRequestBody {
        private Long explosiveId;
        private String tunnelLocation;
        private String purpose;
        private Double quantityRequested;
        private String notes;
    }
}
