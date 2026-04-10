package dsem.backend.repository;

import dsem.backend.model.entity.Explosive;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ExplosiveRepository extends JpaRepository<Explosive, Long> {
    Optional<Explosive> findByCode(String code);
    boolean existsByCode(String code);
}
