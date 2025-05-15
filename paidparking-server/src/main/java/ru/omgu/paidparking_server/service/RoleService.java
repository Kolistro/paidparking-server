package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.omgu.paidparking_server.dto.response.RoleResponseDto;
import ru.omgu.paidparking_server.entity.RoleEntity;
import ru.omgu.paidparking_server.enums.Role;
import ru.omgu.paidparking_server.exception.RoleAlreadyExistsException;
import ru.omgu.paidparking_server.exception.RoleNotFoundException;
import ru.omgu.paidparking_server.mapper.RoleMapper;
import ru.omgu.paidparking_server.repository.RoleRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;

    public RoleResponseDto addRole(Role role){
        if(roleRepo.existsByRole(role)){
            throw new RoleAlreadyExistsException("Роль " + role + " уже существует.");
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole(role);
        roleRepo.save(roleEntity);
        return roleMapper.toDto(roleEntity);
    }

    public List<RoleResponseDto> getAllRole(){
        List<RoleEntity> roles = roleRepo.findAll();
        return roleMapper.toDto(roles);
    }

    public Long delete(Role role){
        RoleEntity roleEntity = roleRepo.findByRole(role)
                .orElseThrow(() -> new RoleNotFoundException("Роль " + role + " не существует."));
        roleRepo.delete(roleEntity);
        return roleEntity.getId();
    }
}
