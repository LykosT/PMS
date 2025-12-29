package gr.lykost.pms.repository;

import gr.lykost.pms.model.EmployeeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTaskRepository extends JpaRepository<EmployeeTask, Long> {

    List<EmployeeTask> findByEmployee_Id(Long employeeId);

    List<EmployeeTask> findByTask_Id(Long taskId);

    long countById(Long id);

}
