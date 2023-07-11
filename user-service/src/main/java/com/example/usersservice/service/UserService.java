package com.example.usersservice.service;

import com.example.usersservice.dto.UserDto;
import com.example.usersservice.entity.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
