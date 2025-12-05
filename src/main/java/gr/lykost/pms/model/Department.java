package gr.lykost.pms.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "departments")
public class Department extends AbstractTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @OneToMany(
            mappedBy = "department",
            fetch = FetchType.LAZY
    )
    private Set<Employee> employees = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
