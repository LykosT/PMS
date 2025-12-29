package gr.lykost.pms.repository;

import gr.lykost.pms.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    long countProjectsById(Long departmentId);

    Optional<Project> findByUuid(String uuid);

    boolean existsByName(String Name);

    List<Project> findByNameContainingIgnoreCase(String name);

    List<Project> findByDepartment_Id(Long departmentId);

    List<Project> findByTeams_Id(Long teamId);

    List<Project> findByEmployees_Id(Long employeeId);
}
