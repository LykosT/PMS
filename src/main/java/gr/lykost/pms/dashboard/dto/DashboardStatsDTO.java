package gr.lykost.pms.dashboard.dto;

public record DashboardStatsDTO(
        long employeeCount,
        long departmentCount,
        long projectCount,
        long teamCount,
        long taskCount,
        long businessRoleCount
) {
}
