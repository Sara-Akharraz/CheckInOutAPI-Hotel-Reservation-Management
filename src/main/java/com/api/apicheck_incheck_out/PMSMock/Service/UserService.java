package com.api.apicheck_incheck_out.PMSMock.Service;

import com.api.apicheck_incheck_out.Dto.UserDto;

import java.util.List;

public interface UserService {
    public List<UserDto> getUsers();
    public UserDto getUser(Long id);
    public UserDto addUser(UserDto userDto);
    public void deleteUser(Long id);
    public void updateUser(Long id, UserDto userDto);
}