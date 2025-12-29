package gr.lykost.pms.repository;

import gr.lykost.pms.core.enums.TaskStatus;
import gr.lykost.pms.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByUuid(String uuid);

    List<Task> findByProject_Id(Long projectId);

    List<Task> findByAssignedToEmployee_Id(Long employeeId);

    List<Task> findByStatus(String status);

    long countById(Long id);
}
