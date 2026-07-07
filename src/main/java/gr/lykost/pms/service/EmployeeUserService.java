package gr.lykost.pms.service;

import gr.lykost.pms.core.exceptions.DuplicateResourceException;
import gr.lykost.pms.core.exceptions.ResourceNotFoundException;
import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.dto.editdto.EmployeeUserEditDTO;
import gr.lykost.pms.dto.readonlydto.EmployeeReadDTO;
import gr.lykost.pms.mapper.EmployeeMapper;
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
    @Transactional
    public EmployeeReadDTO createNewEmployee(EmployeeUserCreateDTO dto) {

        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Employee",
                    "Employee with Email " + dto.getEmail() + " already exists");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("User",
                    "Username " + dto.getUsername() + " already exists");
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

        return employeeMapper.mapToEmployeeReadDTO(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeReadDTO> findAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::mapToEmployeeReadDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {

        Employee employee = getEmployeeOrThrow(id);

        if (employee.getUser() != null) {
            userRepository.delete(employee.getUser());
            log.info("Deleted User account for Employee ID: {}", id);
        }

        employeeRepository.delete(employee);
        log.info("Deleted Employee with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeReadDTO findEmployeeById(Long id) {
        return employeeMapper.mapToEmployeeReadDTO(getEmployeeOrThrow(id));
    }

    @Override
    @Transactional
    public EmployeeReadDTO updateEmployee(Long id, EmployeeUserEditDTO dto) {
        Employee employee = getEmployeeOrThrow(id);

        employeeRepository.findByEmail(dto.getEmail())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("Employee",
                            "Employee with Email " + dto.getEmail() + " already exists");
                });

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setSeniorityLevel(dto.getSeniorityLevel());
        employee.setEmployeeStatus(dto.getEmployeeStatus());

        User user = employee.getUser();
        if (user != null) {
            userRepository.findByUsername(dto.getUsername())
                    .filter(other -> !other.getId().equals(user.getId()))
                    .ifPresent(other -> {
                        throw new DuplicateResourceException("User",
                                "Username " + dto.getUsername() + " already exists");
                    });

            user.setUsername(dto.getUsername());
            user.setSystemRole(dto.getSystemRole());
            user.setActive(dto.isActive());

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userRepository.save(user);
        }
        return employeeMapper.mapToEmployeeReadDTO(employeeRepository.save(employee));
    }

    private Employee getEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",
                        "Employee not found with ID: " + id));
    }
}
