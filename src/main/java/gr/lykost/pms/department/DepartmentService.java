package gr.lykost.pms.department;

import gr.lykost.pms.shared.exception.DuplicateResourceException;
import gr.lykost.pms.shared.exception.ResourceNotFoundException;
import gr.lykost.pms.department.dto.DepartmentCreateDTO;
import gr.lykost.pms.department.dto.DepartmentReadDTO;
import gr.lykost.pms.department.Department;
import gr.lykost.pms.employee.Employee;
import gr.lykost.pms.department.DepartmentRepository;
import gr.lykost.pms.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<DepartmentReadDTO> findAll() {
        return departmentRepository.findAllByOrderByNameAsc().stream()
                .map(this::toReadDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentReadDTO findById(Long id) {
        return toReadDTO(getOrThrow(id));
    }

    @Transactional
    public DepartmentReadDTO create(DepartmentCreateDTO dto) {
        if (departmentRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Department",
                    "Department with name " + dto.getName() + " already exists");
        }
        Department department = new Department();
        apply(department, dto);
        return toReadDTO(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentReadDTO update(Long id, DepartmentCreateDTO dto) {
        Department department = getOrThrow(id);
        departmentRepository.findByName(dto.getName())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("Department",
                            "Department with name " + dto.getName() + " already exists");
                });
        apply(department, dto);
        return toReadDTO(departmentRepository.save(department));
    }

    @Transactional
    public void delete(Long id) {
        departmentRepository.delete(getOrThrow(id));
        // Flush inside the transaction so FK violations surface as
        // DataIntegrityViolationException (mapped to 409) instead of a commit-time 500.
        departmentRepository.flush();
    }

    private void apply(Department department, DepartmentCreateDTO dto) {
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        Employee manager = employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee",
                        "Employee not found with ID: " + dto.getManagerId()));
        department.setManager(manager);
    }

    private Department getOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department",
                        "Department not found with ID: " + id));
    }

    private DepartmentReadDTO toReadDTO(Department department) {
        return new DepartmentReadDTO(
                department.getId(),
                department.getUuid(),
                department.getName(),
                department.getDescription(),
                department.getManager() != null ? department.getManager().getId() : null,
                department.getManager() != null ? department.getManager().getFullName() : null,
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}
