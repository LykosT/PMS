package gr.lykost.pms.dto.createdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCreateDTO {

    @NotBlank(message = "Department name is required")
    private String name;

    private String description;

    @NotNull(message = "Manager ID is required")
    private Long managerId;
}