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
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final TunnelRequestRepository tunnelRequestRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final AuditService auditService;

    public List<Dispatch> getAllDispatches() {
        return dispatchRepository.findAll();
    }

    public Dispatch getById(Long id) {
        return dispatchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispatch not found: " + id));
    }

    @Transactional
    public Dispatch dispatch(Long requestId, String vehicleNumber,
                             String driverName, Double quantity) {
        TunnelRequest request = tunnelRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        // Business Rule: Cannot dispatch without approval
        if (request.getStatus() != RequestStatus.APPROVED) {
            throw new IllegalStateException("Request must be APPROVED before dispatch");
        }

        // Business Rule: Cannot dispatch twice
        if (dispatchRepository.existsByTunnelRequestId(requestId)) {
            throw new IllegalStateException("This request has already been dispatched");
        }

        // Deduct from sub-magazine inventory
        inventoryService.deductStock(
                request.getExplosive().getId(),
                MagazineType.SUB,
                quantity);

        User issuer = getCurrentUser();
        Dispatch dispatch = Dispatch.builder()
                .tunnelRequest(request)
                .issuedBy(issuer)
                .vehicleNumber(vehicleNumber)
                .driverName(driverName)
                .quantityDispatched(quantity)
                .build();

        request.setStatus(RequestStatus.DISPATCHED);
        tunnelRequestRepository.save(request);
        Dispatch saved = dispatchRepository.save(dispatch);

        auditService.log("EXPLOSIVES_DISPATCHED", "Dispatch", saved.getId(),
                String.format("{\"requestId\":%d, \"vehicle\":\"%s\", \"driver\":\"%s\", \"qty\":%s}",
                        requestId, vehicleNumber, driverName, quantity));
        return saved;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
