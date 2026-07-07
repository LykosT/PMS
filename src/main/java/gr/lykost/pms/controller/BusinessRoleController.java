package gr.lykost.pms.controller;

import gr.lykost.pms.dto.createdto.BusinessRoleCreateDTO;
import gr.lykost.pms.dto.readonlydto.BusinessRoleReadDTO;
import gr.lykost.pms.service.BusinessRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-roles")
@RequiredArgsConstructor
public class BusinessRoleController {

    private final BusinessRoleService businessRoleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @GetMapping
    public List<BusinessRoleReadDTO> list() {
        return businessRoleService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public BusinessRoleReadDTO get(@PathVariable Long id) {
        return businessRoleService.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessRoleReadDTO create(@Valid @RequestBody BusinessRoleCreateDTO dto) {
        return businessRoleService.create(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public BusinessRoleReadDTO update(@PathVariable Long id, @Valid @RequestBody BusinessRoleCreateDTO dto) {
        return businessRoleService.update(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        businessRoleService.delete(id);
    }
}
