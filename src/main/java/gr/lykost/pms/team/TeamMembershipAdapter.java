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
import java.util.List;
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
        return teamRepository.findByEmployees_Id(employeeId).stream()
                .map(t -> new NamedRef(t.getId(), t.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void assign(Long employeeId, Set<Long> teamIds) {
        Set<Long> wanted = teamIds == null ? new HashSet<>() : teamIds;

        List<Team> wantedTeams = teamRepository.findAllById(wanted);
        Set<Long> foundIds = wantedTeams.stream().map(Team::getId).collect(Collectors.toSet());
        for (Long teamId : wanted) {
            if (!foundIds.contains(teamId)) {
                throw new ResourceNotFoundException("Team", "Team not found with ID: " + teamId);
            }
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee",
                        "Employee not found with ID: " + employeeId));

        List<Team> currentTeams = teamRepository.findByEmployees_Id(employeeId);
        Set<Long> currentIds = currentTeams.stream().map(Team::getId).collect(Collectors.toSet());

        for (Team team : currentTeams) {
            if (!wanted.contains(team.getId())) {
                team.getEmployees().removeIf(e -> e.getId().equals(employeeId));
                teamRepository.save(team);
            }
        }

        for (Team team : wantedTeams) {
            if (!currentIds.contains(team.getId())) {
                team.getEmployees().add(employee);
                teamRepository.save(team);
            }
        }
    }
}
