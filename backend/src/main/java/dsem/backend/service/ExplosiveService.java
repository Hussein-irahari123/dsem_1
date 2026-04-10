package dsem.backend.service;

import dsem.backend.model.entity.Explosive;
import dsem.backend.repository.ExplosiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExplosiveService {

    private final ExplosiveRepository explosiveRepository;

    public List<Explosive> getAll() {
        return explosiveRepository.findAll();
    }

    public Explosive getById(Long id) {
        return explosiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Explosive not found: " + id));
    }

    public Explosive create(Explosive explosive) {
        if (explosiveRepository.existsByCode(explosive.getCode())) {
            throw new IllegalArgumentException("Explosive code already exists: " + explosive.getCode());
        }
        return explosiveRepository.save(explosive);
    }

    public Explosive update(Long id, Explosive updated) {
        Explosive existing = getById(id);
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setSupplier(updated.getSupplier());
        existing.setUnit(updated.getUnit());
        existing.setDescription(updated.getDescription());
        return explosiveRepository.save(existing);
    }
}
