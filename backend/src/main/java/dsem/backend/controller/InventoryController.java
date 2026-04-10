package dsem.backend.controller;

import dsem.backend.model.entity.Inventory;
import dsem.backend.model.enums.MagazineType;
import dsem.backend.service.InventoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/magazine/{type}")
    public ResponseEntity<List<Inventory>> getByMagazine(@PathVariable MagazineType type) {
        return ResponseEntity.ok(inventoryService.getByMagazine(type));
    }

    @PostMapping("/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
    public ResponseEntity<Inventory> receive(@RequestBody ReceiveRequest req) {
        return ResponseEntity.ok(
            inventoryService.receiveExplosives(req.getExplosiveId(), req.getMagazineType(), req.getQuantity())
        );
    }

    @Data
    static class ReceiveRequest {
        private Long explosiveId;
        private MagazineType magazineType;
        private Double quantity;
    }
}
