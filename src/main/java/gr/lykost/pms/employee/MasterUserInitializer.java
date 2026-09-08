package gr.lykost.pms.employee;

import gr.lykost.pms.shared.enums.EmployeeStatus;
import gr.lykost.pms.shared.enums.SeniorityLevel;
import gr.lykost.pms.shared.enums.SystemRole;
import gr.lykost.pms.employee.dto.EmployeeUserCreateDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder; // Import this
import org.springframework.stereotype.Component;

/***
 * This class initializes the master admin user on application startup.
 */

@Component
@RequiredArgsConstructor
@Transactional
public class MasterUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder; // Inject the Bean

    @Override
    public void run(ApplicationArguments args) {

        // If admin user already exists, only migrate a legacy plaintext password to BCrypt
        var existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            if (!admin.getPassword().startsWith("$2")) {
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
                userRepository.save(admin);
            }
            return;
        }

        // Create default admin user.
        // Built via no-arg constructor + named setters (not the positional
        // @AllArgsConstructor) so future fields added to this DTO can't silently
        // transpose with each other here; businessRoleIds, departmentId and teamIds
        // are deliberately left unset (null), which is correct for the seeded admin
        // (no roles, no department, no teams).
        EmployeeUserCreateDTO dto = new EmployeeUserCreateDTO();
        dto.setFirstName("System");
        dto.setLastName("Administrator");
        dto.setEmail("admin@admin.admin");
        dto.setPhone(null);
        dto.setSeniorityLevel(SeniorityLevel.MANAGER);
        dto.setEmployeeStatus(EmployeeStatus.ACTIVE);
        dto.setUsername("admin");
        dto.setPassword("admin");
        dto.setSystemRole(SystemRole.ADMIN);
        dto.setActive(true);

        Employee employee = Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .seniorityLevel(dto.getSeniorityLevel())
                .employeeStatus(dto.getEmployeeStatus())
                .build();

        employeeRepository.save(employee);

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setSystemRole(dto.getSystemRole());
        user.setActive(dto.isActive());
        user.setEmployee(employee);

        userRepository.save(user);
    }
}
