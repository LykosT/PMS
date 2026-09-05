package gr.lykost.pms.task;

import gr.lykost.pms.shared.enums.TaskStatus;
import gr.lykost.pms.employee.Employee;
import gr.lykost.pms.project.Project;
import gr.lykost.pms.task.Task;
import gr.lykost.pms.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByUuid(String uuid);

    List<Task> findByProject_Id(Long projectId);

    List<Task> findByProject(Project project);

    List<Task> findByTeam(Team team);

//    List<Task> findByAssignee(Employee assignee);

    List<Task> findByTaskStatus(TaskStatus status);

    long countById(Long id);
}
