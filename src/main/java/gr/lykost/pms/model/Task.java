package gr.lykost.pms.model;

import gr.lykost.pms.core.enums.Priority;
import gr.lykost.pms.core.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "tasks")
//public class Task extends AbstractEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(length = 2000)
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TaskStatus TaskStatus;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Priority priority;
//
//    @Column
//    private LocalDate dueDate;
//
//    // Task -> Project (N:1)
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "project_id", nullable = false)
//    private Project project;
//
//    // Task -> Assignee (Employee)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "assignee_id")
//    private Employee assignee;
//
//    // Task -> Team (N:1)
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "team_id", nullable = false)
//    private Team team;
//}
