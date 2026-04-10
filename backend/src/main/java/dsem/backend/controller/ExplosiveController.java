package dsem.backend.controller;

import dsem.backend.model.entity.Explosive;
import dsem.backend.service.ExplosiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/explosives")
@RequiredArgsConstructor
public class ExplosiveController {

    private final ExplosiveService explosiveService;

    @GetMapping
    public ResponseEntity<List<Explosive>> getAll() {
        return ResponseEntity.ok(explosiveService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Explosive> getById(@PathVariable Long id) {
        return ResponseEntity.ok(explosiveService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Explosive> create(@RequestBody Explosive explosive) {
        return ResponseEntity.ok(explosiveService.create(explosive));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Explosive> update(@PathVariable Long id, @RequestBody Explosive explosive) {
        return ResponseEntity.ok(explosiveService.update(id, explosive));
    }
}
