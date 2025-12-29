package gr.lykost.pms.repository;

import gr.lykost.pms.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByUuid(String uuid);

    List<Team> findByProject_Id(Long projectId);

    List<Team> findByEmployees_Id(Long employeeId);

    List<Team> findByNameContainingIgnoreCase(String name);

    long countById(Long id);
}
