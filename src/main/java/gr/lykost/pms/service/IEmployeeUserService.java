package gr.lykost.pms.service;

import gr.lykost.pms.core.exceptions.DuplicateResourceException;
import gr.lykost.pms.core.exceptions.InvalidOperationException;
import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.model.Employee;
import org.jspecify.annotations.Nullable;

import java.util.List;


public interface IEmployeeUserService {
    Employee createNewEmployee(EmployeeUserCreateDTO employeeUserCreateDTO)
            throws DuplicateResourceException, InvalidOperationException;

    List<Employee> findAllEmployees();

    void deleteEmployee(Long id);

    Employee findEmployeeById(Long id);

    Employee updateEmployee(Long id, EmployeeUserCreateDTO employeeDto);
}