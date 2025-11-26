package com.api.apicheck_incheck_out.service;


import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;

import java.util.List;

public interface UserService {
    UserDto getUser(Long id);
    List<UserDto> getAllUsers();
    List<UserDto> getReceptionists();
    List<UserDto> getClients();
    UserDto register(UserDto userDto);
    void deleteUser(Long id);
    UserDto updateUser(Long id, UserDto user);
    List<User> getAdmins();
    String verify(UserDto user);
}
