package dsem.backend.data;

import dsem.backend.model.entity.Explosive;
import dsem.backend.model.entity.Inventory;
import dsem.backend.model.entity.User;
import dsem.backend.model.enums.MagazineType;
import dsem.backend.model.enums.Role;
import dsem.backend.repository.ExplosiveRepository;
import dsem.backend.repository.InventoryRepository;
import dsem.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExplosiveRepository explosiveRepository;
    private final InventoryRepository inventoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedUsers();
            seedExplosives();
            seedInitialInventory();
        }
    }

    private void seedUsers() {
        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .role(Role.ADMIN)
                .active(true)
                .phoneNumber("+250780000000")
                .build());

        userRepository.save(User.builder()
                .username("manager")
                .password(passwordEncoder.encode("manager123"))
                .fullName("Stock Manager")
                .role(Role.STORE_MANAGER)
                .active(true)
                .phoneNumber("+250780000001")
                .build());

        userRepository.save(User.builder()
                .username("ops")
                .password(passwordEncoder.encode("ops123"))
                .fullName("Operations Supervisor")
                .role(Role.OPERATIONS)
                .active(true)
                .phoneNumber("+250780000002")
                .build());
    }

    private void seedExplosives() {
        explosiveRepository.save(Explosive.builder()
                .code("ANFO-001")
                .name("ANFO (Ammonium Nitrate Fuel Oil)")
                .type("Main Charge")
                .unit("KG")
                .supplier("ExploCorp")
                .description("Standard mining explosive")
                .build());

        explosiveRepository.save(Explosive.builder()
                .code("DET-NONEL-01")
                .name("Non-Electric Detonator (25ms)")
                .type("Initiator")
                .unit("PCS")
                .supplier("DetoNation")
                .description("Delay detonators")
                .build());

        explosiveRepository.save(Explosive.builder()
                .code("BOOST-400")
                .name("Pentolite Booster (400g)")
                .type("Booster")
                .unit("PCS")
                .supplier("ExploCorp")
                .description("High velocity booster")
                .build());
    }

    private void seedInitialInventory() {
        Explosive anfo = explosiveRepository.findByCode("ANFO-001").orElse(null);
        Explosive det = explosiveRepository.findByCode("DET-NONEL-01").orElse(null);

        if (anfo != null) {
            inventoryRepository.save(Inventory.builder()
                    .explosive(anfo)
                    .magazineType(MagazineType.MAIN)
                    .quantityInStock(5000.0)
                    .build());
            
            inventoryRepository.save(Inventory.builder()
                    .explosive(anfo)
                    .magazineType(MagazineType.SUB)
                    .quantityInStock(200.0)
                    .build());
        }

        if (det != null) {
            inventoryRepository.save(Inventory.builder()
                    .explosive(det)
                    .magazineType(MagazineType.MAIN)
                    .quantityInStock(1000.0)
                    .build());
            
            inventoryRepository.save(Inventory.builder()
                    .explosive(det)
                    .magazineType(MagazineType.SUB)
                    .quantityInStock(50.0)
                    .build());
        }
    }
}
