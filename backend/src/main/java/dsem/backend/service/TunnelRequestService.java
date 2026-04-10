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
public class TunnelRequestService {

    private final TunnelRequestRepository tunnelRequestRepository;
    private final ExplosiveRepository explosiveRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final AuditService auditService;

    public List<TunnelRequest> getAllRequests() {
        return tunnelRequestRepository.findAll();
    }

    public List<TunnelRequest> getPendingRequests() {
        return tunnelRequestRepository.findByStatus(RequestStatus.PENDING);
    }

    public List<TunnelRequest> getMyRequests() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return tunnelRequestRepository.findByRequestedById(user.getId());
    }

    public TunnelRequest getById(Long id) {
        return tunnelRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
    }

    @Transactional
    public TunnelRequest createRequest(Long explosiveId, String tunnelLocation,
                                       String purpose, Double quantity, String notes) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Explosive explosive = explosiveRepository.findById(explosiveId)
                .orElseThrow(() -> new RuntimeException("Explosive not found: " + explosiveId));

        TunnelRequest request = TunnelRequest.builder()
                .requestedBy(user)
                .explosive(explosive)
                .tunnelLocation(tunnelLocation)
                .purpose(purpose)
                .quantityRequested(quantity)
                .notes(notes)
                .status(RequestStatus.PENDING)
                .build();

        TunnelRequest saved = tunnelRequestRepository.save(request);
        auditService.log("REQUEST_CREATED", "TunnelRequest", saved.getId(),
                String.format("{\"explosive\":\"%s\", \"quantity\":%s, \"location\":\"%s\"}",
                        explosive.getName(), quantity, tunnelLocation));
        return saved;
    }
}
