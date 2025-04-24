package com.api.apicheck_incheck_out.Service;


import com.api.apicheck_incheck_out.DTO.UserDto;

import java.util.List;

public interface UserService {
    public UserDto logIn(String username, String password);
    public UserDto register(UserDto userDto);
    public UserDto getUser(Long id);
    public List<UserDto> getAllUsers();
    public List<UserDto> getReceptionists();
    public List<UserDto> getClients();
    public void deleteUser(Long id);
    public UserDto updateUser(Long id, UserDto user);

    String verify(UserDto user);
}
