package dsem.backend.service;

import dsem.backend.model.entity.*;
import dsem.backend.model.enums.MagazineType;
import dsem.backend.model.enums.RequestStatus;
import dsem.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final TunnelRequestRepository tunnelRequestRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final AuditService auditService;

    public List<Approval> getAllApprovals() {
        return approvalRepository.findAll();
    }

    @Transactional
    public Approval approveRequest(Long requestId, String comment) {
        TunnelRequest request = tunnelRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not in PENDING state");
        }

        // Business Rule: Cannot approve if stock is insufficient
        boolean hasStock = inventoryService.hasSufficientStock(
                request.getExplosive().getId(),
                MagazineType.SUB,
                request.getQuantityRequested());

        if (!hasStock) {
            throw new IllegalStateException(
                    "Insufficient stock in sub-magazine for " + request.getExplosive().getName());
        }

        User approver = getCurrentUser();
        Approval approval = Approval.builder()
                .tunnelRequest(request)
                .approvedBy(approver)
                .status(RequestStatus.APPROVED)
                .comment(comment)
                .build();

        request.setStatus(RequestStatus.APPROVED);
        tunnelRequestRepository.save(request);
        Approval saved = approvalRepository.save(approval);

        auditService.log("REQUEST_APPROVED", "Approval", saved.getId(),
                String.format("{\"requestId\":%d, \"approver\":\"%s\"}", requestId, approver.getUsername()));
        return saved;
    }

    @Transactional
    public Approval rejectRequest(Long requestId, String comment) {
        TunnelRequest request = tunnelRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not in PENDING state");
        }

        User approver = getCurrentUser();
        Approval approval = Approval.builder()
                .tunnelRequest(request)
                .approvedBy(approver)
                .status(RequestStatus.REJECTED)
                .comment(comment)
                .build();

        request.setStatus(RequestStatus.REJECTED);
        tunnelRequestRepository.save(request);
        Approval saved = approvalRepository.save(approval);

        auditService.log("REQUEST_REJECTED", "Approval", saved.getId(),
                String.format("{\"requestId\":%d, \"reason\":\"%s\"}", requestId, comment));
        return saved;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
