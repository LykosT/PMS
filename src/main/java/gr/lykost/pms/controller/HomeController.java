package gr.lykost.pms.controller;

import gr.lykost.pms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EmployeeRepository employeeRepository;

    @GetMapping({"/", "/home"})
    public String index(Model model) {

        long count = employeeRepository.count();
        model.addAttribute("employeeCount", count);
        model.addAttribute("view", "fragments/dashboard");
        return "index";
    }
}