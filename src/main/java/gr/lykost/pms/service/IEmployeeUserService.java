package gr.lykost.pms.service;

import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.dto.editdto.EmployeeUserEditDTO;
import gr.lykost.pms.dto.readonlydto.EmployeeReadDTO;

import java.util.List;


public interface IEmployeeUserService {

    EmployeeReadDTO createNewEmployee(EmployeeUserCreateDTO employeeUserCreateDTO);

    List<EmployeeReadDTO> findAllEmployees();

    void deleteEmployee(Long id);

    EmployeeReadDTO findEmployeeById(Long id);

    EmployeeReadDTO updateEmployee(Long id, EmployeeUserEditDTO employeeDto);
}
