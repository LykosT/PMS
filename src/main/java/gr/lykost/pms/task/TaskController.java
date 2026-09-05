package gr.lykost.pms.task;

import gr.lykost.pms.task.dto.TaskCreateDTO;
import gr.lykost.pms.task.dto.TaskReadDTO;
import gr.lykost.pms.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @GetMapping
    public List<TaskReadDTO> list() {
        return taskService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public TaskReadDTO get(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskReadDTO create(@Valid @RequestBody TaskCreateDTO dto) {
        return taskService.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public TaskReadDTO update(@PathVariable Long id, @Valid @RequestBody TaskCreateDTO dto) {
        return taskService.update(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
