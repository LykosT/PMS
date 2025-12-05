package gr.lykost.pms.repository;

import gr.lykost.pms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    List<Employee> findByDepartment_Id(Long departmentId);

    List<Employee> findByTeams_Id(Long teamId);
}