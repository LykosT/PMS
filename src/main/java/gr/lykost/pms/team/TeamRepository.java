package gr.lykost.pms.team;

import gr.lykost.pms.department.Department;
import gr.lykost.pms.project.Project;
import gr.lykost.pms.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByUuid(String uuid);

    long countById(Long id);

    List<Team> findByDepartment(Department department);

    List<Team> findByProject(Project project);

    boolean existsByNameAndDepartment(String name, Department department);
}
