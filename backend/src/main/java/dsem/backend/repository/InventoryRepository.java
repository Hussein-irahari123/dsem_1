package dsem.backend.repository;

import dsem.backend.model.entity.Inventory;
import dsem.backend.model.enums.MagazineType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByExplosiveIdAndMagazineType(Long explosiveId, MagazineType magazineType);
    List<Inventory> findByMagazineType(MagazineType magazineType);
    List<Inventory> findByExplosiveId(Long explosiveId);
}
