package gr.lykost.pms.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "departments")
//public class Department extends AbstractEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column
//    private String description;
//
//    // Department -> Employees (1:N)
//    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
//    private Set<Employee> employees = new HashSet<>();
//
//    // Department -> Teams (1:N)
//    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
//    private Set<Team> teams = new HashSet<>();
//
//    // Department -> Projects (1:N)
//    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
//    private Set<Project> projects = new HashSet<>();
//
//    // Department -> Manager
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "manager_id", nullable = false)
//    private Employee manager;
//}
