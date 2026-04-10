package dsem.backend.controller;

import dsem.backend.model.entity.BlastReport;
import dsem.backend.service.BlastService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blast")
@RequiredArgsConstructor
public class BlastController {

    private final BlastService blastService;

    @GetMapping
    public ResponseEntity<List<BlastReport>> getAll() {
        return ResponseEntity.ok(blastService.getAllBlastReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlastReport> getById(@PathVariable Long id) {
        return ResponseEntity.ok(blastService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BlastReport> recordBlast(@RequestBody BlastBody body) {
        return ResponseEntity.ok(blastService.recordBlast(
                body.getDispatchId(),
                body.getHolesDrilled(),
                body.getQuantityUsed(),
                body.getLocation(),
                body.getNotes()
        ));
    }

    @Data
    static class BlastBody {
        private Long dispatchId;
        private Integer holesDrilled;
        private Double quantityUsed;
        private String location;
        private String notes;
    }
}
