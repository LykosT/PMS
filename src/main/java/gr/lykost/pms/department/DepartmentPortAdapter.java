package gr.lykost.pms.department;

import gr.lykost.pms.employee.DepartmentPort;
import gr.lykost.pms.employee.NamedRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class DepartmentPortAdapter implements DepartmentPort {

    private final DepartmentRepository departmentRepository;

    @Override
    public Optional<NamedRef> byId(Long departmentId) {
        if (departmentId == null) {
            return Optional.empty();
        }
        return departmentRepository.findById(departmentId)
                .map(d -> new NamedRef(d.getId(), d.getName()));
    }
}
