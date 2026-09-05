package gr.lykost.pms.employee;

import gr.lykost.pms.employee.dto.EmployeeUserCreateDTO;
import gr.lykost.pms.employee.dto.EmployeeUserEditDTO;
import gr.lykost.pms.employee.dto.EmployeeReadDTO;

import java.util.List;


public interface IEmployeeUserService {

    EmployeeReadDTO createNewEmployee(EmployeeUserCreateDTO employeeUserCreateDTO);

    List<EmployeeReadDTO> findAllEmployees();

    void deleteEmployee(Long id);

    EmployeeReadDTO findEmployeeById(Long id);

    EmployeeReadDTO updateEmployee(Long id, EmployeeUserEditDTO employeeDto);
}
