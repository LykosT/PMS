package gr.lykost.pms.repository;

import gr.lykost.pms.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByTitle(String title);
    Optional<Department> findByUuid(String uuid);
}
