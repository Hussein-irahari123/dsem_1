package dsem.backend.service;

import dsem.backend.dto.request.DemandSummaryDTO;
import dsem.backend.model.entity.Explosive;
import dsem.backend.model.entity.Inventory;
import dsem.backend.model.enums.MagazineType;
import dsem.backend.repository.ExplosiveRepository;
import dsem.backend.repository.InventoryRepository;
import dsem.backend.repository.ReturnRepository;
import dsem.backend.repository.TunnelRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CORE FEATURE: Demand Calculation Engine
 *
 * Computes:
 *   Net Request = TotalApprovedRequests - TotalReturned
 *   Rutongo Request = NetRequest - SubMagazineStock  (if > 0)
 */
@Service
@RequiredArgsConstructor
public class DemandCalculationEngine {

    private final TunnelRequestRepository tunnelRequestRepository;
    private final ReturnRepository returnRepository;
    private final InventoryRepository inventoryRepository;
    private final ExplosiveRepository explosiveRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Calculate demand for ALL explosive types for given date range.
     */
    public List<DemandSummaryDTO> calculateDemand(LocalDateTime from, LocalDateTime to) {
        List<Explosive> explosives = explosiveRepository.findAll();
        List<DemandSummaryDTO> result = new ArrayList<>();

        for (Explosive explosive : explosives) {
            result.add(calculateForExplosive(explosive, from, to));
        }
        return result;
    }

    /**
     * Calculate demand for a specific explosive type.
     */
    public DemandSummaryDTO calculateForExplosive(Long explosiveId, LocalDateTime from, LocalDateTime to) {
        Explosive explosive = explosiveRepository.findById(explosiveId)
                .orElseThrow(() -> new RuntimeException("Explosive not found: " + explosiveId));
        return calculateForExplosive(explosive, from, to);
    }

    private DemandSummaryDTO calculateForExplosive(Explosive explosive, LocalDateTime from, LocalDateTime to) {
        // Step 1: Total approved requests in period
        Double totalRequested = tunnelRequestRepository
                .sumApprovedQuantityByExplosiveBetween(explosive.getId(), from, to);

        // Step 2: Total returned in period
        Double totalReturned = returnRepository
                .sumReturnedQuantityByExplosiveBetween(explosive.getId(), from, to);

        // Step 3: Net Request = Total Requested - Total Returned
        double netRequest = Math.max(0, totalRequested - totalReturned);

        // Step 4: Available stock in SUB magazine
        double availableSubStock = inventoryRepository
                .findByExplosiveIdAndMagazineType(explosive.getId(), MagazineType.SUB)
                .map(Inventory::getQuantityInStock)
                .orElse(0.0);

        // Step 5: Rutongo Request = Net Request - Available Sub Stock (min 0)
        double rutogoRequest = Math.max(0, netRequest - availableSubStock);

        return DemandSummaryDTO.builder()
                .explosiveId(explosive.getId())
                .explosiveName(explosive.getName())
                .explosiveUnit(explosive.getUnit())
                .totalRequested(totalRequested)
                .totalReturned(totalReturned)
                .netRequest(netRequest)
                .availableSubStock(availableSubStock)
                .rutogoRequest(rutogoRequest)
                .fromDate(from.format(FMT))
                .toDate(to.format(FMT))
                .build();
    }
}
