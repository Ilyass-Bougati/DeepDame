package com.deepdame.service.role;

import com.deepdame.dto.role.RoleDto;
import com.deepdame.dto.role.RoleMapper;
import com.deepdame.entity.Role;
import com.deepdame.exception.NotFoundException;
import com.deepdame.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDto save(RoleDto roleDto) {
        Role role = roleMapper.toEntity(roleDto);
        return roleMapper.toDTO(roleRepository.save(role));
    }

    @Override
    public RoleDto update(RoleDto roleDto) {
        Role role = roleRepository.findById(roleDto.getId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        role.setName(roleDto.getName());
        return roleMapper.toDTO(role);
    }

    @Override
    public void delete(UUID uuid) {
        roleRepository.deleteById(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto findById(UUID id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }
}
