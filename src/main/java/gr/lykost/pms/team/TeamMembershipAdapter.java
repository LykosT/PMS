package gr.lykost.pms.team;

import gr.lykost.pms.employee.Employee;
import gr.lykost.pms.employee.EmployeeRepository;
import gr.lykost.pms.employee.TeamMembershipPort;
import gr.lykost.pms.employee.NamedRef;
import gr.lykost.pms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class TeamMembershipAdapter implements TeamMembershipPort {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<NamedRef> teamsOf(Long employeeId) {
        if (employeeId == null) {
            return new HashSet<>();
        }
        return teamRepository.findAll().stream()
                .filter(t -> t.getEmployees().stream()
                        .anyMatch(e -> e.getId().equals(employeeId)))
                .map(t -> new NamedRef(t.getId(), t.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void assign(Long employeeId, Set<Long> teamIds) {
        Set<Long> wanted = teamIds == null ? new HashSet<>() : teamIds;

        for (Long teamId : wanted) {
            if (!teamRepository.existsById(teamId)) {
                throw new ResourceNotFoundException("Team", "Team not found with ID: " + teamId);
            }
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",
                        "Employee not found with ID: " + employeeId));

        for (Team team : teamRepository.findAll()) {
            boolean shouldBeMember = wanted.contains(team.getId());
            boolean isMember = team.getEmployees().stream()
                    .anyMatch(e -> e.getId().equals(employeeId));

            if (shouldBeMember && !isMember) {
                team.getEmployees().add(employee);
                teamRepository.save(team);
            } else if (!shouldBeMember && isMember) {
                team.getEmployees().removeIf(e -> e.getId().equals(employeeId));
                teamRepository.save(team);
            }
        }
    }
}
