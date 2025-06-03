package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.omgu.paidparking_server.dto.request.UserRequestDto;
import ru.omgu.paidparking_server.dto.response.RoleResponseDto;
import ru.omgu.paidparking_server.dto.response.UserResponseDto;
import ru.omgu.paidparking_server.entity.RoleEntity;
import ru.omgu.paidparking_server.entity.UserEntity;
import ru.omgu.paidparking_server.enums.Role;
import ru.omgu.paidparking_server.exception.UserAlreadyExistsException;
import ru.omgu.paidparking_server.exception.UserNotFoundException;
import ru.omgu.paidparking_server.mapper.RoleMapper;
import ru.omgu.paidparking_server.mapper.UserMapper;
import ru.omgu.paidparking_server.repository.UserRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private UserService userService;


    @Test
    void editUser_ShouldEditUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto("John", "Doe", "+79991234567");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFirstName("OldName");
        userEntity.setLastName("OldLastName");
        userEntity.setPhoneNumber("+70000000000");

        when(userRepo.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepo.existsByPhoneNumber("+79991234567")).thenReturn(false);
        when(userMapper.toDto(userEntity)).thenReturn(new UserResponseDto(
                userId, "John", "Doe", "+79991234567"
        ));

        // Act
        UserResponseDto responseDto = userService.editUser(requestDto, userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals("John", responseDto.firstName());
        assertEquals("Doe", responseDto.lastName());
        assertEquals("+79991234567", responseDto.phoneNumber());
        verify(userRepo, times(1)).save(userEntity);
    }

    @Test
    void editUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        UserRequestDto requestDto = new UserRequestDto("John", "Doe", "+79991234567");

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.editUser(requestDto, userId)
        );
        assertEquals("Пользователя c id = 999 не существует.", exception.getMessage());
    }

    @Test
    void editUser_ShouldThrowException_WhenPhoneNumberAlreadyExists() {
        // Arrange
        Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto("John", "Doe", "+79991234567");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepo.existsByPhoneNumber("+79991234567")).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.editUser(requestDto, userId)
        );
        assertEquals("Пользователь с номером+79991234567 уже существует.", exception.getMessage());
    }

    @Test
    void getUserByPhoneNumber_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String phoneNumber = "+79991234567";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setPhoneNumber(phoneNumber);

        when(userRepo.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(new UserResponseDto(
                1L, "John", "Doe", phoneNumber
        ));

        // Act
        UserResponseDto responseDto = userService.getUserByPhoneNumber(phoneNumber);

        // Assert
        assertNotNull(responseDto);
        assertEquals("John", responseDto.firstName());
        assertEquals("Doe", responseDto.lastName());
        assertEquals(phoneNumber, responseDto.phoneNumber());
    }

    @Test
    void getUserByPhoneNumber_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String phoneNumber = "+79991234567";

        when(userRepo.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserByPhoneNumber(phoneNumber)
        );
        assertEquals("Пользователя c номером телефона: +79991234567 не существует.", exception.getMessage());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setPhoneNumber("+79991234567");

        when(userRepo.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(new UserResponseDto(
                userId, "John", "Doe", "+79991234567"
        ));

        // Act
        UserResponseDto responseDto = userService.getUserById(userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals("John", responseDto.firstName());
        assertEquals("Doe", responseDto.lastName());
        assertEquals("+79991234567", responseDto.phoneNumber());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(userId)
        );
        assertEquals("Пользователя c id = 999 не существует.", exception.getMessage());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers_WhenUsersExist() {
        // Arrange
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPhoneNumber("+79991234567");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setPhoneNumber("+79999876543");

        List<UserEntity> userEntities = List.of(user1, user2);

        when(userRepo.findAll()).thenReturn(userEntities);
        when(userMapper.toDto(userEntities)).thenReturn(List.of(
                new UserResponseDto(1L, "John", "Doe", "+79991234567"),
                new UserResponseDto(2L, "Jane", "Smith", "+79999876543")
        ));

        // Act
        List<UserResponseDto> responseDtos = userService.getAllUsers();

        // Assert
        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());
        assertEquals("John", responseDtos.get(0).firstName());
        assertEquals("Jane", responseDtos.get(1).firstName());
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserResponseDto> responseDtos = userService.getAllUsers();

        // Assert
        assertNotNull(responseDtos);
        assertTrue(responseDtos.isEmpty());
    }

    @Test
    void getRolesUserById_ShouldReturnRoles_WhenUserExists() {
        // Arrange
        Long userId = 1L;

        RoleEntity role1 = new RoleEntity();
        role1.setId(1L);
        role1.setRole(Role.ROLE_USER);

        RoleEntity role2 = new RoleEntity();
        role2.setId(2L);
        role2.setRole(Role.ROLE_ADMIN);

        Set<RoleEntity> roles = new HashSet<>(List.of(role1, role2));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setRoles(roles);

        when(userRepo.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleMapper.toDto(roles)).thenReturn(Set.of(
                new RoleResponseDto(1L, "ROLE_USER"),
                new RoleResponseDto(2L, "ROLE_ADMIN")
        ));

        // Act
        Set<RoleResponseDto> responseDtos = userService.getRolesUserById(userId);

        // Assert
        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());
        assertTrue(responseDtos.stream().anyMatch(role -> role.role().equals(Role.ROLE_USER)));
        assertTrue(responseDtos.stream().anyMatch(role -> role.role().equals(Role.ROLE_ADMIN)));
    }

    @Test
    void getRolesUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getRolesUserById(userId)
        );
        assertEquals("Пользователя c id = 999 не существует.", exception.getMessage());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(userEntity));

        // Act
        Long deletedId = userService.deleteUser(userId);

        // Assert
        assertEquals(userId, deletedId);
        verify(userRepo, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(userId)
        );
        assertEquals("Пользователя c id = 999 не существует.", exception.getMessage());
    }
}
