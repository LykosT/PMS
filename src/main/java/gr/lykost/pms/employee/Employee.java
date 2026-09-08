package gr.lykost.pms.employee;

import gr.lykost.pms.businessrole.BusinessRole;
import gr.lykost.pms.shared.AbstractEntity;

import gr.lykost.pms.shared.enums.EmployeeStatus;
import gr.lykost.pms.shared.enums.SeniorityLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    private SeniorityLevel seniorityLevel;

    @Enumerated(EnumType.STRING)
    @Column
    private EmployeeStatus employeeStatus;

    @OneToOne(mappedBy = "employee", fetch = FetchType.EAGER)
    private User user;

    // Employee -> Department, by id only (via DepartmentPort) to avoid a
    // department -> employee -> department module cycle.
    @Column(name = "department_id")
    private Long departmentId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "employee_businessrole",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "businessrole_id")
    )
    @Builder.Default
    private Set<BusinessRole> businessRoles = new HashSet<>();

//    // Employee -> Team (N:1)
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "team_id", nullable = false)
//    private Team team;
//
//    // Employee -> Tasks (1:N)
//    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
//    private Set<Task> tasks = new HashSet<>();

}
