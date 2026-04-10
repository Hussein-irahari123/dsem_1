package dsem.backend.controller;

import dsem.backend.model.entity.Return;
import dsem.backend.service.ReturnService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @GetMapping
    public ResponseEntity<List<Return>> getAll() {
        return ResponseEntity.ok(returnService.getAllReturns());
    }

    @PostMapping
    public ResponseEntity<Return> recordReturn(@RequestBody ReturnBody body) {
        return ResponseEntity.ok(returnService.recordReturn(
                body.getBlastReportId(),
                body.getQuantityReturned()
        ));
    }

    @Data
    static class ReturnBody {
        private Long blastReportId;
        private Double quantityReturned;
    }
}
