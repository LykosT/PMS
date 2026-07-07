package gr.lykost.pms.controller;

import gr.lykost.pms.dto.createdto.DepartmentCreateDTO;
import gr.lykost.pms.dto.readonlydto.DepartmentReadDTO;
import gr.lykost.pms.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @GetMapping
    public List<DepartmentReadDTO> list() {
        return departmentService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public DepartmentReadDTO get(@PathVariable Long id) {
        return departmentService.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentReadDTO create(@Valid @RequestBody DepartmentCreateDTO dto) {
        return departmentService.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public DepartmentReadDTO update(@PathVariable Long id, @Valid @RequestBody DepartmentCreateDTO dto) {
        return departmentService.update(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}
