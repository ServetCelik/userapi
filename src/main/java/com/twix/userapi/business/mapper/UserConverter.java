package com.twix.userapi.business.mapper;

import com.twix.userapi.controller.dto.UserDTO;
import com.twix.userapi.repository.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserConverter {
    private UserConverter(){

    }
    public static UserDTO toUserDTO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        return UserDTO.builder()
                .id(userEntity.getId())
                .userName(userEntity.getUserName())
                .followings(convertUserEntitiesToSharables(userEntity.getFollowings()))
                .followers(convertUserEntitiesToSharables(userEntity.getFollowers()))
                .build();
    }

    private static Set<UserDTO> convertUserEntitiesToSharables(Set<UserEntity> userEntities) {
        if (userEntities == null) {
            return null;
        }

        return userEntities.stream()
                .map(UserConverter::toUserDTO)
                .collect(Collectors.toSet());
    }

}
