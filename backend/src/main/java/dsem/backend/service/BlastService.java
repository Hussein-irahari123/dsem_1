package dsem.backend.service;

import dsem.backend.model.entity.*;
import dsem.backend.model.enums.RequestStatus;
import dsem.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlastService {

    private final BlastReportRepository blastReportRepository;
    private final DispatchRepository dispatchRepository;
    private final TunnelRequestRepository tunnelRequestRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public List<BlastReport> getAllBlastReports() {
        return blastReportRepository.findAll();
    }

    public BlastReport getById(Long id) {
        return blastReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blast report not found: " + id));
    }

    @Transactional
    public BlastReport recordBlast(Long dispatchId, Integer holesDrilled,
                                   Double quantityUsed, String location, String notes) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));

        // Business Rule: Cannot record blast twice for same dispatch
        if (blastReportRepository.existsByDispatchId(dispatchId)) {
            throw new IllegalStateException("Blast already recorded for this dispatch");
        }

        // Business Rule: Cannot use more than was dispatched
        if (quantityUsed > dispatch.getQuantityDispatched()) {
            throw new IllegalArgumentException(
                    String.format("Used quantity (%.2f) exceeds dispatched quantity (%.2f)",
                            quantityUsed, dispatch.getQuantityDispatched()));
        }

        User recorder = getCurrentUser();
        BlastReport report = BlastReport.builder()
                .dispatch(dispatch)
                .recordedBy(recorder)
                .holesDrilled(holesDrilled)
                .quantityUsed(quantityUsed)
                .location(location)
                .notes(notes)
                .build();

        BlastReport saved = blastReportRepository.save(report);

        // Update request status to COMPLETED
        TunnelRequest request = dispatch.getTunnelRequest();
        request.setStatus(RequestStatus.COMPLETED);
        tunnelRequestRepository.save(request);

        auditService.log("BLAST_RECORDED", "BlastReport", saved.getId(),
                String.format("{\"dispatchId\":%d, \"holes\":%d, \"used\":%s, \"location\":\"%s\"}",
                        dispatchId, holesDrilled, quantityUsed, location));
        return saved;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
