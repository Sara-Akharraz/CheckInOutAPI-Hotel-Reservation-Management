package com.api.apicheck_incheck_out.service;


import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;

import java.util.List;

public interface UserService {
    public UserDto logIn(String username, String password);
    public UserDto getUser(Long id);
    public List<UserDto> getAllUsers();
    public List<UserDto> getReceptionists();
    public List<UserDto> getClients();
    public String getUserNameById(Long userId);
    public UserDto register(UserDto userDto);

    public void deleteUser(Long id);
    public UserDto updateUser(Long id, UserDto user);
    public List<User> getAdmins();
    String verify(UserDto user);
}
