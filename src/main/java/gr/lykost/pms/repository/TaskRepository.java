package gr.lykost.pms.repository;

import gr.lykost.pms.core.enums.TaskStatus;
import gr.lykost.pms.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject_Id(Long projectId);

    List<Task> findByAssignee_Id(Long employeeId);

    List<Task> findByStatus(TaskStatus status);

}
