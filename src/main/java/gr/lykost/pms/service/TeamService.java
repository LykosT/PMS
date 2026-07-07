package gr.lykost.pms.service;

import gr.lykost.pms.core.exceptions.ResourceNotFoundException;
import gr.lykost.pms.dto.createdto.TeamCreateDTO;
import gr.lykost.pms.dto.readonlydto.TeamReadDTO;
import gr.lykost.pms.model.Department;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.model.Project;
import gr.lykost.pms.model.Team;
import gr.lykost.pms.repository.DepartmentRepository;
import gr.lykost.pms.repository.EmployeeRepository;
import gr.lykost.pms.repository.ProjectRepository;
import gr.lykost.pms.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<TeamReadDTO> findAll() {
        return teamRepository.findAll().stream()
                .map(this::toReadDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamReadDTO findById(Long id) {
        return toReadDTO(getOrThrow(id));
    }

    @Transactional
    public TeamReadDTO create(TeamCreateDTO dto) {
        Team team = new Team();
        apply(team, dto);
        return toReadDTO(teamRepository.save(team));
    }

    @Transactional
    public TeamReadDTO update(Long id, TeamCreateDTO dto) {
        Team team = getOrThrow(id);
        apply(team, dto);
        return toReadDTO(teamRepository.save(team));
    }

    @Transactional
    public void delete(Long id) {
        teamRepository.delete(getOrThrow(id));
        teamRepository.flush();
    }

    private void apply(Team team, TeamCreateDTO dto) {
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department",
                        "Department not found with ID: " + dto.getDepartmentId()));
        team.setDepartment(department);

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project",
                        "Project not found with ID: " + dto.getProjectId()));
        team.setProject(project);

        if (dto.getLeadId() != null) {
            Employee lead = employeeRepository.findById(dto.getLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee",
                            "Employee not found with ID: " + dto.getLeadId()));
            team.setLead(lead);
        } else {
            team.setLead(null);
        }
    }

    private Team getOrThrow(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team",
                        "Team not found with ID: " + id));
    }

    private TeamReadDTO toReadDTO(Team team) {
        return new TeamReadDTO(
                team.getId(),
                team.getUuid(),
                team.getName(),
                team.getDescription(),
                team.getDepartment().getId(),
                team.getDepartment().getName(),
                team.getProject().getId(),
                team.getProject().getName(),
                team.getLead() != null ? team.getLead().getId() : null,
                team.getLead() != null ? team.getLead().getFullName() : null,
                team.getCreatedAt(),
                team.getUpdatedAt()
        );
    }
}
