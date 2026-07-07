package gr.lykost.pms.controller;

import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.dto.editdto.EmployeeUserEditDTO;
import gr.lykost.pms.dto.readonlydto.EmployeeReadDTO;
import gr.lykost.pms.service.IEmployeeUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeUserController {

    private final IEmployeeUserService employeeUserService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @GetMapping
    public List<EmployeeReadDTO> listEmployees() {
        return employeeUserService.findAllEmployees();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public EmployeeReadDTO getEmployee(@PathVariable Long id) {
        return employeeUserService.findEmployeeById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeReadDTO createEmployee(@Valid @RequestBody EmployeeUserCreateDTO employeeDto) {
        return employeeUserService.createNewEmployee(employeeDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public EmployeeReadDTO updateEmployee(@PathVariable Long id,
                                          @Valid @RequestBody EmployeeUserEditDTO employeeDto) {
        return employeeUserService.updateEmployee(id, employeeDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {
        employeeUserService.deleteEmployee(id);
    }
}
