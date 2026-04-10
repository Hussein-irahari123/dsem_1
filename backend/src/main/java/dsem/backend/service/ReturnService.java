package dsem.backend.service;

import dsem.backend.model.entity.*;
import dsem.backend.model.enums.MagazineType;
import dsem.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final BlastReportRepository blastReportRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final AuditService auditService;

    public List<Return> getAllReturns() {
        return returnRepository.findAll();
    }

    @Transactional
    public Return recordReturn(Long blastReportId, Double quantityReturned) {
        BlastReport blastReport = blastReportRepository.findById(blastReportId)
                .orElseThrow(() -> new RuntimeException("Blast report not found: " + blastReportId));

        // Prevent duplicate returns
        if (returnRepository.existsByBlastReportId(blastReportId)) {
            throw new IllegalStateException("Return already recorded for this blast report");
        }

        Explosive explosive = blastReport.getDispatch().getTunnelRequest().getExplosive();
        double maxReturnable = blastReport.getDispatch().getQuantityDispatched()
                               - blastReport.getQuantityUsed();

        if (quantityReturned > maxReturnable) {
            throw new IllegalArgumentException(
                    String.format("Return qty (%.2f) exceeds returnable qty (%.2f)",
                            quantityReturned, maxReturnable));
        }

        User receiver = getCurrentUser();
        Return ret = Return.builder()
                .blastReport(blastReport)
                .receivedBy(receiver)
                .quantityReturned(quantityReturned)
                .build();

        Return saved = returnRepository.save(ret);

        // Add returned stock back to sub-magazine
        inventoryService.addStock(explosive.getId(), MagazineType.SUB, quantityReturned);

        auditService.log("EXPLOSIVES_RETURNED", "Return", saved.getId(),
                String.format("{\"blastReportId\":%d, \"returned\":%s, \"explosive\":\"%s\"}",
                        blastReportId, quantityReturned, explosive.getName()));
        return saved;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
