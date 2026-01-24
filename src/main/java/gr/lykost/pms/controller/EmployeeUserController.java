package gr.lykost.pms.controller;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import gr.lykost.pms.core.enums.SystemRole;
import gr.lykost.pms.core.exceptions.DuplicateResourceException;
import gr.lykost.pms.core.exceptions.InvalidOperationException;
import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.dto.readonlydto.EmployeeReadDTO;
import gr.lykost.pms.mapper.EmployeeMapper;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.repository.UserRepository;
import gr.lykost.pms.service.IEmployeeUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeUserController {

    private final IEmployeeUserService employeeUserService;
    private final EmployeeMapper employeeMapper;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/create")
    public String showCreateEmployeeForm(Model model) {
        populateFormAttributes(model);
        model.addAttribute("employeeDto", new EmployeeUserCreateDTO());
        model.addAttribute("view", "employee-form");
        return "index";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    @GetMapping("/view")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeUserService.findAllEmployees());
        model.addAttribute("view", "employee-view");
        return "index";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/create")
    public String createEmployee(@Valid @ModelAttribute("employeeDto") EmployeeUserCreateDTO employeeDto,
                                 BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        Employee savedEmployee;

        if (bindingResult.hasErrors()) {
            populateFormAttributes(model);
            model.addAttribute("view", "employee-form");
            return "index";
        }
        try {
            savedEmployee = employeeUserService.createNewEmployee(employeeDto);
            EmployeeReadDTO employeeReadDTO = employeeMapper.mapToEmployeeReadDTO(savedEmployee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee " + employeeReadDTO.getFirstName() + " " +
                    employeeReadDTO.getLastName() + " created successfully!");
            return "redirect:/employee/view";

        }catch (DuplicateResourceException | InvalidOperationException e) {
            populateFormAttributes(model);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("view", "employee-form");
            return "index";
        }
    }
    private void populateFormAttributes(Model model) {
        model.addAttribute("systemRoles", SystemRole.values());
        model.addAttribute("seniorityLevels", SeniorityLevel.values());
        model.addAttribute("employeeStatuses", EmployeeStatus.values());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeUserService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete employee.");
        }
        return "redirect:/employee/view";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeUserService.findEmployeeById(id);

        EmployeeUserCreateDTO dto = new EmployeeUserCreateDTO();
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setSeniorityLevel(employee.getSeniorityLevel());
        dto.setEmployeeStatus(employee.getEmployeeStatus());

        if (employee.getUser() != null) {
            dto.setUsername(employee.getUser().getUsername());
            dto.setSystemRole(employee.getUser().getSystemRole());
            dto.setActive(employee.getUser().isEnabled());
        }

        model.addAttribute("employeeDto", dto);
        model.addAttribute("employeeId", id);

        populateFormAttributes(model);
        model.addAttribute("view", "employee-form");
        return "index";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employeeDto") EmployeeUserCreateDTO employeeDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employeeId", id);
            populateFormAttributes(model);
            model.addAttribute("view", "employee-form");
            return "index";
        }

        try {
            employeeUserService.updateEmployee(id, employeeDto);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
            return "redirect:/employee/view";
        } catch (Exception e) {
            model.addAttribute("employeeId", id);
            populateFormAttributes(model);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("view", "employee-form");
            return "index";
        }
    }
}