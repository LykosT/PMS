package gr.lykost.pms.employee;

import gr.lykost.pms.businessrole.BusinessRole;
import gr.lykost.pms.businessrole.BusinessRoleRepository;
import gr.lykost.pms.shared.exception.DuplicateResourceException;
import gr.lykost.pms.shared.exception.ResourceNotFoundException;
import gr.lykost.pms.employee.dto.EmployeeUserCreateDTO;
import gr.lykost.pms.employee.dto.EmployeeUserEditDTO;
import gr.lykost.pms.employee.dto.EmployeeReadDTO;
import gr.lykost.pms.employee.EmployeeMapper;
import gr.lykost.pms.employee.Employee;
import gr.lykost.pms.employee.User;
import gr.lykost.pms.employee.EmployeeRepository;
import gr.lykost.pms.employee.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeUserService implements IEmployeeUserService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final BusinessRoleRepository businessRoleRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentPort departmentPort;
    private final TeamMembershipPort teamMembershipPort;

    private Set<BusinessRole> resolveBusinessRoles(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return ids.stream()
                .map(id -> businessRoleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("BusinessRole",
                                "Business role not found with ID: " + id)))
                .collect(java.util.stream.Collectors.toSet());
    }

    private Long resolveDepartmentId(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        departmentPort.byId(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department",
                        "Department not found with ID: " + departmentId));
        return departmentId;
    }

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
        employee.setBusinessRoles(resolveBusinessRoles(dto.getBusinessRoleIds()));
        employee.setDepartmentId(resolveDepartmentId(dto.getDepartmentId()));
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created with ID: {}", savedEmployee.getId());

        teamMembershipPort.assign(savedEmployee.getId(), dto.getTeamIds());

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
        if (dto.getBusinessRoleIds() != null) {
            employee.setBusinessRoles(resolveBusinessRoles(dto.getBusinessRoleIds()));
        }
        employee.setDepartmentId(resolveDepartmentId(dto.getDepartmentId()));

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
        if (dto.getTeamIds() != null) {
            teamMembershipPort.assign(employee.getId(), dto.getTeamIds());
        }
        return employeeMapper.mapToEmployeeReadDTO(employeeRepository.save(employee));
    }

    private Employee getEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",
                        "Employee not found with ID: " + id));
    }
}
