package gr.lykost.pms.repository;

import gr.lykost.pms.model.Department;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.model.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    long countById(Long employeeId);

    long countByDepartmentId(Long departmentId);

    boolean existsByEmail(String email);

    Optional<Employee> findByUuid(String uuid);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByDepartment(Department department);

    List<Employee> findByTeam(Team team);

    List<Employee> findByActiveTrue();

    List<Employee> findByDepartment_Id(Long departmentId);

    List<Employee> findByTeam_Id(Long teamId);
}