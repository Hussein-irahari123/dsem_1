package dsem.backend.service;

import dsem.backend.model.entity.*;
import dsem.backend.model.enums.MagazineType;
import dsem.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ExplosiveRepository explosiveRepository;
    private final AuditService auditService;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getByMagazine(MagazineType magazineType) {
        return inventoryRepository.findByMagazineType(magazineType);
    }

    public Inventory getByExplosiveAndMagazine(Long explosiveId, MagazineType magazineType) {
        return inventoryRepository.findByExplosiveIdAndMagazineType(explosiveId, magazineType)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for explosive ID: " + explosiveId));
    }

    @Transactional
    public Inventory receiveExplosives(Long explosiveId, MagazineType magazineType, Double quantity) {
        Explosive explosive = explosiveRepository.findById(explosiveId)
                .orElseThrow(() -> new RuntimeException("Explosive not found: " + explosiveId));

        Inventory inventory = inventoryRepository
                .findByExplosiveIdAndMagazineType(explosiveId, magazineType)
                .orElse(Inventory.builder()
                        .explosive(explosive)
                        .magazineType(magazineType)
                        .quantityInStock(0.0)
                        .build());

        inventory.setQuantityInStock(inventory.getQuantityInStock() + quantity);
        Inventory saved = inventoryRepository.save(inventory);

        auditService.log("EXPLOSIVES_RECEIVED",
                "Inventory", saved.getId(),
                String.format("{\"explosive\":\"%s\", \"quantity\":%s, \"magazine\":\"%s\"}",
                        explosive.getName(), quantity, magazineType));
        return saved;
    }

    @Transactional
    public void deductStock(Long explosiveId, MagazineType magazineType, Double quantity) {
        Inventory inventory = getByExplosiveAndMagazine(explosiveId, magazineType);
        if (inventory.getQuantityInStock() < quantity) {
            throw new IllegalStateException(
                    String.format("Insufficient stock. Available: %.2f, Requested: %.2f",
                            inventory.getQuantityInStock(), quantity));
        }
        inventory.setQuantityInStock(inventory.getQuantityInStock() - quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void addStock(Long explosiveId, MagazineType magazineType, Double quantity) {
        Inventory inventory = getByExplosiveAndMagazine(explosiveId, magazineType);
        inventory.setQuantityInStock(inventory.getQuantityInStock() + quantity);
        inventoryRepository.save(inventory);
    }

    public boolean hasSufficientStock(Long explosiveId, MagazineType magazineType, Double quantity) {
        return inventoryRepository.findByExplosiveIdAndMagazineType(explosiveId, magazineType)
                .map(inv -> inv.getQuantityInStock() >= quantity)
                .orElse(false);
    }
}
