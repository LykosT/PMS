package gr.lykost.pms.service;

import gr.lykost.pms.core.exceptions.ResourceNotFoundException;
import gr.lykost.pms.dto.createdto.TaskCreateDTO;
import gr.lykost.pms.dto.readonlydto.TaskReadDTO;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.model.Project;
import gr.lykost.pms.model.Task;
import gr.lykost.pms.model.Team;
import gr.lykost.pms.repository.EmployeeRepository;
import gr.lykost.pms.repository.ProjectRepository;
import gr.lykost.pms.repository.TaskRepository;
import gr.lykost.pms.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<TaskReadDTO> findAll() {
        return taskRepository.findAll().stream()
                .map(this::toReadDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskReadDTO findById(Long id) {
        return toReadDTO(getOrThrow(id));
    }

    @Transactional
    public TaskReadDTO create(TaskCreateDTO dto) {
        Task task = new Task();
        apply(task, dto);
        return toReadDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskReadDTO update(Long id, TaskCreateDTO dto) {
        Task task = getOrThrow(id);
        apply(task, dto);
        return toReadDTO(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        taskRepository.delete(getOrThrow(id));
        taskRepository.flush();
    }

    private void apply(Task task, TaskCreateDTO dto) {
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(dto.getTaskStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project",
                        "Project not found with ID: " + dto.getProjectId()));
        task.setProject(project);

        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team",
                        "Team not found with ID: " + dto.getTeamId()));
        task.setTeam(team);

        if (dto.getAssigneeId() != null) {
            Employee assignee = employeeRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee",
                            "Employee not found with ID: " + dto.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }
    }

    private Task getOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task",
                        "Task not found with ID: " + id));
    }

    private TaskReadDTO toReadDTO(Task task) {
        return new TaskReadDTO(
                task.getId(),
                task.getUuid(),
                task.getName(),
                task.getDescription(),
                task.getTaskStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getTeam().getId(),
                task.getTeam().getName(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getFullName() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
