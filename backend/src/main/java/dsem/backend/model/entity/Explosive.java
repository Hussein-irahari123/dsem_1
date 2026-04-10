package dsem.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "explosives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Explosive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(length = 150)
    private String supplier;

    @Column(nullable = false, length = 20)
    private String unit; // kg, pcs, box

    @Column(length = 500)
    private String description;
}
