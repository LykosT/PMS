package gr.lykost.pms.service;

import gr.lykost.pms.core.exceptions.DuplicateResourceException;
import gr.lykost.pms.core.exceptions.ResourceNotFoundException;
import gr.lykost.pms.dto.createdto.BusinessRoleCreateDTO;
import gr.lykost.pms.dto.readonlydto.BusinessRoleReadDTO;
import gr.lykost.pms.model.BusinessRole;
import gr.lykost.pms.repository.BusinessRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessRoleService {

    private final BusinessRoleRepository businessRoleRepository;

    @Transactional(readOnly = true)
    public List<BusinessRoleReadDTO> findAll() {
        return businessRoleRepository.findAll().stream()
                .map(this::toReadDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessRoleReadDTO findById(Long id) {
        return toReadDTO(getOrThrow(id));
    }

    @Transactional
    public BusinessRoleReadDTO create(BusinessRoleCreateDTO dto) {
        if (businessRoleRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("BusinessRole",
                    "Business role with name " + dto.getName() + " already exists");
        }
        BusinessRole role = new BusinessRole();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        return toReadDTO(businessRoleRepository.save(role));
    }

    @Transactional
    public BusinessRoleReadDTO update(Long id, BusinessRoleCreateDTO dto) {
        BusinessRole role = getOrThrow(id);
        businessRoleRepository.findByName(dto.getName())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("BusinessRole",
                            "Business role with name " + dto.getName() + " already exists");
                });
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        return toReadDTO(businessRoleRepository.save(role));
    }

    @Transactional
    public void delete(Long id) {
        businessRoleRepository.delete(getOrThrow(id));
        businessRoleRepository.flush();
    }

    private BusinessRole getOrThrow(Long id) {
        return businessRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusinessRole",
                        "Business role not found with ID: " + id));
    }

    private BusinessRoleReadDTO toReadDTO(BusinessRole role) {
        return new BusinessRoleReadDTO(
                role.getId(),
                role.getUuid(),
                role.getName(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}
