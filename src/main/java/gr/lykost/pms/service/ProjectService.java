package gr.lykost.pms.service;

import gr.lykost.pms.core.exceptions.ResourceNotFoundException;
import gr.lykost.pms.dto.createdto.ProjectCreateDTO;
import gr.lykost.pms.dto.readonlydto.ProjectReadDTO;
import gr.lykost.pms.model.Department;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.model.Project;
import gr.lykost.pms.repository.DepartmentRepository;
import gr.lykost.pms.repository.EmployeeRepository;
import gr.lykost.pms.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<ProjectReadDTO> findAll() {
        return projectRepository.findAll().stream()
                .map(this::toReadDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectReadDTO findById(Long id) {
        return toReadDTO(getOrThrow(id));
    }

    @Transactional
    public ProjectReadDTO create(ProjectCreateDTO dto) {
        Project project = new Project();
        apply(project, dto);
        return toReadDTO(projectRepository.save(project));
    }

    @Transactional
    public ProjectReadDTO update(Long id, ProjectCreateDTO dto) {
        Project project = getOrThrow(id);
        apply(project, dto);
        return toReadDTO(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id) {
        projectRepository.delete(getOrThrow(id));
        projectRepository.flush();
    }

    private void apply(Project project, ProjectCreateDTO dto) {
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setProjectStatus(dto.getProjectStatus());
        project.setPriority(dto.getPriority());
        project.setStartDate(dto.getStartDate());

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department",
                        "Department not found with ID: " + dto.getDepartmentId()));
        project.setDepartment(department);

        Employee manager = employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee",
                        "Employee not found with ID: " + dto.getManagerId()));
        project.setManager(manager);
    }

    private Project getOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project",
                        "Project not found with ID: " + id));
    }

    private ProjectReadDTO toReadDTO(Project project) {
        return new ProjectReadDTO(
                project.getId(),
                project.getUuid(),
                project.getName(),
                project.getDescription(),
                project.getProjectStatus(),
                project.getPriority(),
                project.getStartDate(),
                project.getDepartment().getId(),
                project.getDepartment().getName(),
                project.getManager() != null ? project.getManager().getId() : null,
                project.getManager() != null ? project.getManager().getFullName() : null,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
