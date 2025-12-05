package gr.lykost.pms.repository;

import gr.lykost.pms.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByUuid(String uuid);

    boolean existsByTitle(String title);

    List<Project> findByManager_Id(Long managerId);

    List<Project> findByEmployees_Id(Long employeeId);
}
