package gr.lykost.pms.repository;

import gr.lykost.pms.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByDepartment_Id(Long departmentId);
    List<Team> findByEmployees_Id(Long employeeId);

    Optional<Team> findByUuid(String uuid);
}
