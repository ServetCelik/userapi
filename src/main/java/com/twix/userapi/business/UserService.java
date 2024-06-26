package com.twix.userapi.business;

import com.twix.userapi.controller.CreateUserRequest;
import com.twix.userapi.controller.dto.UserDTO;
import com.twix.userapi.repository.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Long createUser(CreateUserRequest user);
    Optional<String>  loginUser(String name,String password);
    Long deleteUser(Long userId);
    Long updateUser(Long id, UserEntity userEntity);
    UserDTO getUserById(Long id);
    UserDTO getUserByUserName(String name);
    List<UserEntity> getAllUsers();
    void followUser(Long userId, Long followId);
    void unfollowUser(Long userId, Long unfollowId);


}
