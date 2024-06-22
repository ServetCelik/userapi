package com.twix.userapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    public Long id;
    public String userName;
    public Set<UserDTO> followings;
    public Set<UserDTO> followers;
}
