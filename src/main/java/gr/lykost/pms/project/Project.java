package gr.lykost.pms.project;

import gr.lykost.pms.shared.AbstractEntity;
import gr.lykost.pms.employee.Employee;
import gr.lykost.pms.department.Department;

import gr.lykost.pms.shared.enums.Priority;
import gr.lykost.pms.shared.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "projects")
public class Project extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus projectStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column
    private LocalDate startDate;

    // Project -> Department (N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Project -> Manager (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;

    // Project -> Teams
    // Commented out to avoid a module cycle (project would depend on team,
    // while team already depends on project).
//    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<Team> teams = new HashSet<>();

    // Project -> Tasks
    // Commented out to avoid a module cycle (project would depend on task,
    // while task already depends on project).
//    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
//    @Builder.Default
//    private Set<Task> tasks = new HashSet<>();

}
