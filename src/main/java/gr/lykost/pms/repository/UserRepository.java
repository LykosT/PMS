package gr.lykost.pms.repository;

import gr.lykost.pms.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUuid(String uuid);

    @EntityGraph(attributePaths = "employee")
    Optional<User> findByUsername(String username);

    List<User> findByIsActiveTrue();
}
