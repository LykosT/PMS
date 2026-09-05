package gr.lykost.pms.businessrole;

import gr.lykost.pms.businessrole.BusinessRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRoleRepository extends JpaRepository<BusinessRole, Long> {

    Optional<BusinessRole> findByUuid(String uuid);

    Optional<BusinessRole> findByName(String name);

    boolean existsByName(String name);
}
