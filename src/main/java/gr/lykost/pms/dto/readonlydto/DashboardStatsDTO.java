package gr.lykost.pms.dto.readonlydto;

public record DashboardStatsDTO(
        long employeeCount,
        long departmentCount,
        long projectCount,
        long teamCount,
        long taskCount,
        long businessRoleCount
) {
}
