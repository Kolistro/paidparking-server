package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.omgu.paidparking_server.dto.response.RoleResponseDto;
import ru.omgu.paidparking_server.entity.RoleEntity;
import ru.omgu.paidparking_server.enums.Role;
import ru.omgu.paidparking_server.exception.RoleAlreadyExistsException;
import ru.omgu.paidparking_server.exception.RoleNotFoundException;
import ru.omgu.paidparking_server.mapper.RoleMapper;
import ru.omgu.paidparking_server.repository.RoleRepo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    @Test
    void addRole_ShouldAddRole_WhenRoleDoesNotExist() {
        // Arrange
        Role role = Role.ROLE_USER;

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1L);
        roleEntity.setRole(role);

        when(roleRepo.existsByRole(role)).thenReturn(false);
        when(roleMapper.toDto(any(RoleEntity.class))).thenReturn(new RoleResponseDto(1L, role));

        // Act
        RoleResponseDto responseDto = roleService.addRole(role);

        // Assert
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.id());
        assertEquals(Role.ROLE_USER, responseDto.role());

        // Capture the saved entity
        ArgumentCaptor<RoleEntity> captor = ArgumentCaptor.forClass(RoleEntity.class);
        verify(roleRepo).save(captor.capture());

        RoleEntity savedEntity = captor.getValue();
        assertNotNull(savedEntity);
        assertEquals(Role.ROLE_USER, savedEntity.getRole());
    }

    @Test
    void addRole_ShouldThrowException_WhenRoleAlreadyExists() {
        // Arrange
        Role role = Role.ROLE_USER;

        when(roleRepo.existsByRole(role)).thenReturn(true);

        // Act & Assert
        RoleAlreadyExistsException exception = assertThrows(
                RoleAlreadyExistsException.class,
                () -> roleService.addRole(role)
        );
        assertEquals("Роль ROLE_USER уже существует.", exception.getMessage());
    }

    @Test
    void getAllRole_ShouldReturnListOfRoles_WhenRolesExist() {
        // Arrange
        RoleEntity role1 = new RoleEntity();
        role1.setId(1L);
        role1.setRole(Role.ROLE_USER);

        RoleEntity role2 = new RoleEntity();
        role2.setId(2L);
        role2.setRole(Role.ROLE_ADMIN);

        List<RoleEntity> roles = List.of(role1, role2);

        when(roleRepo.findAll()).thenReturn(roles);
        when(roleMapper.toDto(roles)).thenReturn(List.of(
                new RoleResponseDto(1L, Role.ROLE_USER),
                new RoleResponseDto(2L, Role.ROLE_ADMIN)
        ));

        // Act
        List<RoleResponseDto> responseDtos = roleService.getAllRole();

        // Assert
        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());
        assertEquals(Role.ROLE_USER, responseDtos.get(0).role());
        assertEquals(Role.ROLE_ADMIN, responseDtos.get(1).role());
    }

    @Test
    void getAllRole_ShouldReturnEmptyList_WhenNoRolesExist() {
        // Arrange
        when(roleRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<RoleResponseDto> responseDtos = roleService.getAllRole();

        // Assert
        assertNotNull(responseDtos);
        assertTrue(responseDtos.isEmpty());
    }

    @Test
    void delete_ShouldDeleteRole_WhenRoleExists() {
        // Arrange
        Role role = Role.ROLE_USER;

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1L);
        roleEntity.setRole(role);

        when(roleRepo.findByRole(role)).thenReturn(Optional.of(roleEntity));

        // Act
        Long deletedId = roleService.delete(role);

        // Assert
        assertEquals(1L, deletedId);
        verify(roleRepo, times(1)).delete(roleEntity);
    }

    @Test
    void delete_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        Role role = Role.ROLE_USER;

        when(roleRepo.findByRole(role)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.delete(role)
        );
        assertEquals("Роль ROLE_USER не существует.", exception.getMessage());
    }
}