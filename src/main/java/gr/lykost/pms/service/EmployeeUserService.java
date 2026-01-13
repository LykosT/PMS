package gr.lykost.pms.service; // Adjust package

import gr.lykost.pms.core.exceptions.DuplicateResourceException;
import gr.lykost.pms.core.exceptions.InvalidOperationException;
import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.mapper.EmployeeMapper;
import gr.lykost.pms.mapper.UserMapper;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.model.User;
import gr.lykost.pms.repository.EmployeeRepository;
import gr.lykost.pms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeUserService implements IEmployeeUserService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Employee createNewEmployee(EmployeeUserCreateDTO dto)
            throws DuplicateResourceException, InvalidOperationException {

        try {
            if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Employee", "Employee with Email " + dto.getEmail() + " already exists");
            }
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new DuplicateResourceException("User", "Username " + dto.getUsername() + " already exists");
            }

            Employee employee = employeeMapper.mapToEmployee(dto);
            Employee savedEmployee = employeeRepository.save(employee);
            log.info("Employee created with ID: {}", savedEmployee.getId());

            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setSystemRole(dto.getSystemRole());
            user.setActive(dto.isActive());

            user.setEmployee(savedEmployee);
            userRepository.save(user);

            savedEmployee.setUser(user);

            log.info("User created successfully for Employee ID: {}", savedEmployee.getId());

            return savedEmployee;

        } catch (DuplicateResourceException e) {
            log.error("Duplicate resource error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating employee: {}", e.getMessage());
            throw new InvalidOperationException("Employee", "Could not create Employee due to an unexpected error.");
        }
    }

    @Override
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        if (employee.getUser() != null) {
            userRepository.delete(employee.getUser());
            log.info("Deleted User account for Employee ID: {}", id);
        }

        employeeRepository.delete(employee);
        log.info("Deleted Employee with ID: {}", id);
    }

    @Override
    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, EmployeeUserCreateDTO dto) {
        Employee employee = findEmployeeById(id);

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setSeniorityLevel(dto.getSeniorityLevel());
        employee.setEmployeeStatus(dto.getEmployeeStatus());

        if (employee.getUser() != null) {
            User user = employee.getUser();
            user.setUsername(dto.getUsername());
            user.setSystemRole(dto.getSystemRole());
            user.setActive(dto.isActive());

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userRepository.save(user);
        }
        return employeeRepository.save(employee);
    }
}