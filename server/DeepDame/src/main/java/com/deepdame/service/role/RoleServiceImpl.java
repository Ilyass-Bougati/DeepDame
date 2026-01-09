package com.deepdame.service.role;

import com.deepdame.dto.role.RoleDto;
import com.deepdame.dto.role.RoleMapper;
import com.deepdame.entity.Role;
import com.deepdame.exception.ConflictException;
import com.deepdame.exception.NotFoundException;
import com.deepdame.exception.Unauthorized;
import com.deepdame.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
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

        if ("SUPER-ADMIN".equalsIgnoreCase(role.getName())) {
            throw new Unauthorized("The system role SUPER-ADMIN cannot be renamed.");
        }

        String newName = roleDto.getName().trim();
        if (!role.getName().equalsIgnoreCase(newName) && roleRepository.existsByName(newName)) {
            throw new ConflictException("The role name '" + newName + "' already exists.");
        }

        role.setName(roleDto.getName());
        return roleMapper.toDTO(role);
    }

    @Override
    public void delete(UUID uuid) {
        Role role = roleRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        if ("SUPER-ADMIN".equalsIgnoreCase(role.getName())) {
            throw new IllegalStateException("The SUPER-ADMIN role is a core system component and cannot be deleted.");
        }

        roleRepository.deleteById(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto findById(UUID id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll(){
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void createRole(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            throw new ConflictException("Role already exists");
        }
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
    }
}
