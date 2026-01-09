package gr.lykost.pms.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Builder
//@Table(name = "businessroles")
//public class BusinessRole extends AbstractEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String name;
//
//    @Column
//    private String description;
//
//    @ManyToMany(mappedBy = "businessRoles", fetch = FetchType.LAZY)
//    private Set<Employee> employees = new HashSet<>();
//
//}