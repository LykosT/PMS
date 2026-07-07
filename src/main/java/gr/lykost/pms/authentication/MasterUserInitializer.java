package gr.lykost.pms.authentication;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import gr.lykost.pms.core.enums.SystemRole;
import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.model.User;
import gr.lykost.pms.repository.EmployeeRepository;
import gr.lykost.pms.repository.UserRepository;
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

        // Create default admin user
        EmployeeUserCreateDTO dto = new EmployeeUserCreateDTO(
                "System",
                "Administrator",
                "admin@admin.admin",
                null,
                SeniorityLevel.MANAGER,
                EmployeeStatus.ACTIVE,
                "admin",
                "admin",
                SystemRole.ADMIN,
                true
        );

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