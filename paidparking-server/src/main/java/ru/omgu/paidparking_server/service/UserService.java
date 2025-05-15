package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.omgu.paidparking_server.dto.request.UserRequestDto;
import ru.omgu.paidparking_server.dto.response.RoleResponseDto;
import ru.omgu.paidparking_server.dto.response.UserResponseDto;
import ru.omgu.paidparking_server.entity.RoleEntity;
import ru.omgu.paidparking_server.entity.UserEntity;
import ru.omgu.paidparking_server.exception.UserAlreadyExistsException;
import ru.omgu.paidparking_server.exception.UserNotFoundException;
import ru.omgu.paidparking_server.mapper.RoleMapper;
import ru.omgu.paidparking_server.mapper.UserMapper;
import ru.omgu.paidparking_server.repository.UserRepo;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public UserResponseDto editUser(UserRequestDto user, Long userId){
        UserEntity userEntity = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + userId + " не существует."));
        if(userRepo.existsByPhoneNumber(user.phoneNumber())){
            throw new UserAlreadyExistsException("Пользователь с номером" + user.phoneNumber() + " уже существует.");
        }

        userEntity.setFirstName(user.firstName());
        userEntity.setLastName(user.lastName());
        userEntity.setPhoneNumber(user.phoneNumber());
        userRepo.save(userEntity);
        return userMapper.toDto(userEntity);
    }

    public UserResponseDto getUserByPhoneNumber(String phoneNumber){
        UserEntity userEntity = userRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c номером телефона: "
                        + phoneNumber + " не существует."));
        return userMapper.toDto(userEntity);
    }

    public UserResponseDto getUserById(Long id){
        UserEntity userEntity = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + id + " не существует."));
        return userMapper.toDto(userEntity);
    }

    public List<UserResponseDto> getAllUsers(){
        List<UserEntity> userEntities = userRepo.findAll();
        return userMapper.toDto(userEntities);
    }

    // ToDo в каком сервесе лучше оставить этот метод, у пользователя? или у роли?
    public Set<RoleResponseDto> getRolesUserById(Long id){
        UserEntity userEntity = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + id + " не существует."));
        Set<RoleEntity> roles = userEntity.getRoles();
        return roleMapper.toDto(roles);
    }

    public Long deleteUser(Long id){
        userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя c id = " + id + " не существует."));
        userRepo.deleteById(id);
        return id;
    }
}
