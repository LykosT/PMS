package gr.lykost.pms.department;

import gr.lykost.pms.shared.AbstractEntity;
import gr.lykost.pms.employee.Employee;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "departments")
public class Department extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    // Department -> Employees (1:N)
    // Re-enable together with Employee.department (owning side, currently commented out)
//    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<Employee> employees = new HashSet<>();

    // Department -> Teams (1:N)
    // Commented out to avoid a module cycle (department would depend on team,
    // while team already depends on department). Restore alongside a
    // module-boundary-safe way to expose the inverse side if ever needed.
//    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<Team> teams = new HashSet<>();

    // Department -> Projects (1:N)
    // Commented out to avoid a module cycle (department would depend on
    // project, while project already depends on department).
//    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<Project> projects = new HashSet<>();

    // Department -> Manager
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;
}
