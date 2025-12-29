package gr.lykost.pms.repository;

import gr.lykost.pms.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, Long> {

    Optional<EmployeeRole> findByUuid(String uuid);

    Optional<EmployeeRole> findByName(String name);

    List<EmployeeRole> findByEmployee_Id(Long employeeId);

    long countById(Long id);

}
