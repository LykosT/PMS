package gr.lykost.pms.repository;

import gr.lykost.pms.model.Department;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    long countDepartmentById(Long departmentId);

    List<Department> findAllByOrderByNameAsc();

    Optional<Department> findByName(String name);

    Optional<Department> findByUuid(String uuid);

    boolean existsByName(String name);
}