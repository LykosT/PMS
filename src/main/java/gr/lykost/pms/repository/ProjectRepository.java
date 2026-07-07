package gr.lykost.pms.repository;

import gr.lykost.pms.core.enums.ProjectStatus;
import gr.lykost.pms.model.Department;
import gr.lykost.pms.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    long countProjectsById(Long departmentId);

    Optional<Project> findByUuid(String uuid);

    List<Project> findByDepartment_Id(Long departmentId);

//    List<Project> findByDepartment(Department department);

    List<Project> findByProjectStatus(ProjectStatus status);

}
