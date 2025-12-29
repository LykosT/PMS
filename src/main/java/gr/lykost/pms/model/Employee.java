package gr.lykost.pms.model;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employees")
public class Employee extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Transient
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column
    private SeniorityLevel seniority;

    @Enumerated(EnumType.STRING)
    @Column
    private EmployeeStatus employeeStatus;

    @Column(nullable = false)
    private boolean active = true;

    // Employee -> Department (N:1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    // Employee <-> User (1:1, inverse)
    @OneToOne(mappedBy = "employee", fetch = FetchType.EAGER)
    private User user;

    // Employee <-> EmployeeRole (M:N)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "employee_role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<EmployeeRole> employeeRoles = new HashSet<>();

    // Employee <-> Team (M:N, inverse)
    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    private Set<Team> teams = new HashSet<>();

    // Employee <-> Task (via join entity)
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private Set<EmployeeTask> employeeTasks = new HashSet<>();

}
