package gr.lykost.pms.repository;

//import gr.lykost.pms.core.enums.TaskStatus;
//import gr.lykost.pms.model.Employee;
//import gr.lykost.pms.model.Project;
//import gr.lykost.pms.model.Task;
//import gr.lykost.pms.model.Team;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface TaskRepository extends JpaRepository<Task, Long> {
//
//    Optional<Task> findByUuid(String uuid);
//
//    List<Task> findByProject_Id(Long projectId);
//
//    List<Task> findByProject(Project project);
//
//    List<Task> findByTeam(Team team);
//
////    List<Task> findByAssignee(Employee assignee);
//
//    List<Task> findByTaskStatus(TaskStatus status);
//
//    long countById(Long id);
//}
