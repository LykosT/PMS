package gr.lykost.pms.controller;

import gr.lykost.pms.dto.createdto.ProjectCreateDTO;
import gr.lykost.pms.dto.readonlydto.ProjectReadDTO;
import gr.lykost.pms.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @GetMapping
    public List<ProjectReadDTO> list() {
        return projectService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public ProjectReadDTO get(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectReadDTO create(@Valid @RequestBody ProjectCreateDTO dto) {
        return projectService.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ProjectReadDTO update(@PathVariable Long id, @Valid @RequestBody ProjectCreateDTO dto) {
        return projectService.update(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}
