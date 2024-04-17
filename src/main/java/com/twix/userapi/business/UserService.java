package com.twix.userapi.business;

import com.twix.userapi.controller.CreateUserRequest;
import com.twix.userapi.repository.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Long createUser(CreateUserRequest user);
    Long deleteUser(Long userId);
    Long updateUser(Long id, UserEntity userEntity);
    Optional<UserEntity> getUserById(Long id);
    Optional<UserEntity> getUserByUserName(String name);
    List<UserEntity> GetAllUsers();


}
