package gr.lykost.pms.model;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


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

//    // Employee -> Department (N:1)
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "department_id", nullable = false)
//    private Department department;
//
//    // Employee <-> BusinessRole (M:N)
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "employee_businessrole",
//            joinColumns = @JoinColumn(name = "employee_id"),
//            inverseJoinColumns = @JoinColumn(name = "businessrole_id")
//    )
//    private Set<BusinessRole> businessRoles = new HashSet<>();
//
//    // Employee -> Team (N:1)
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "team_id", nullable = false)
//    private Team team;
//
//    // Employee -> Tasks (1:N)
//    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
//    private Set<Task> tasks = new HashSet<>();

}
