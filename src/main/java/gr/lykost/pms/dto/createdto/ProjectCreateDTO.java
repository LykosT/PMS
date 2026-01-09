package gr.lykost.pms.dto.createdto;
//
//import gr.lykost.pms.core.enums.Priority;
//import gr.lykost.pms.core.enums.ProjectStatus;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class ProjectCreateDTO {
//
//    @NotBlank(message = "Project name is required")
//    private String name;
//
//    private String description;
//
//    @NotNull(message = "Project status is required")
//    private ProjectStatus projectStatus;
//
//    @NotNull(message = "Priority is required")
//    private Priority priority;
//
//    private LocalDate startDate;
//
//    @NotNull(message = "Department ID is required")
//    private Long departmentId;
//
//    @NotNull(message = "Manager ID is required")
//    private Long managerId;
//}