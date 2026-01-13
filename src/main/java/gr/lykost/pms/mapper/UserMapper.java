package gr.lykost.pms.mapper;

import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User mapToUser(EmployeeUserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setSystemRole(dto.getSystemRole());
        user.setActive(true);
        return user;
    }
}
