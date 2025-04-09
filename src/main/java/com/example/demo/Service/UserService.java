package com.example.demo.Service;

import com.example.demo.Dto.UserDto;
import com.example.demo.Entity.User;

import java.util.List;

public interface UserService {
    public UserDto logIn(String username, String password);
    public void logOut(Long id);
    public UserDto getUser(Long id);
    public List<UserDto> getAllUsers();
    public List<UserDto> getReceptionists();
    public List<UserDto> getClients();
    public void deleteUser(Long id);
    public UserDto updateUser(Long id, UserDto user);
}
