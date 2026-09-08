package gr.lykost.pms.team;

import gr.lykost.pms.shared.AbstractEntity;
import gr.lykost.pms.employee.Employee;
import gr.lykost.pms.department.Department;
import gr.lykost.pms.project.Project;

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
@Table(name = "teams")
public class Team extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    // Team -> Department (N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Team -> Project (N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Team -> Lead
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Employee lead;

    // Team <-> Employees (M:N), owned here so Employee needs no import of
    // the team module (which would create a module cycle, since team
    // already depends on employee). See employee.TeamMembershipPort.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "employee_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @Builder.Default
    private Set<Employee> employees = new HashSet<>();

    // Team -> Tasks (1:N)
    // Commented out to avoid a module cycle (team would depend on task,
    // while task already depends on team).
//    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<Task> tasks = new HashSet<>();
}
