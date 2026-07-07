package gr.lykost.pms.controller;

import gr.lykost.pms.dto.readonlydto.DashboardStatsDTO;
import gr.lykost.pms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final BusinessRoleRepository businessRoleRepository;

    @GetMapping
    public DashboardStatsDTO stats() {
        return new DashboardStatsDTO(
                employeeRepository.count(),
                departmentRepository.count(),
                projectRepository.count(),
                teamRepository.count(),
                taskRepository.count(),
                businessRoleRepository.count()
        );
    }
}
