package dsem.backend.controller;

import dsem.backend.model.entity.Dispatch;
import dsem.backend.service.DispatchService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'STORE_MANAGER')")
public class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping
    public ResponseEntity<List<Dispatch>> getAll() {
        return ResponseEntity.ok(dispatchService.getAllDispatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispatch> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dispatchService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Dispatch> dispatch(@RequestBody DispatchBody body) {
        return ResponseEntity.ok(dispatchService.dispatch(
                body.getRequestId(),
                body.getVehicleNumber(),
                body.getDriverName(),
                body.getQuantityDispatched()
        ));
    }

    @Data
    static class DispatchBody {
        private Long requestId;
        private String vehicleNumber;
        private String driverName;
        private Double quantityDispatched;
    }
}
