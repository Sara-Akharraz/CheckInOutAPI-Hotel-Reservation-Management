package com.api.apicheck_incheck_out.PMSMock.Service.Impl;

import com.api.apicheck_incheck_out.Dto.UserDto;
import com.api.apicheck_incheck_out.PMSMock.Model.ChambreModel;
import com.api.apicheck_incheck_out.PMSMock.Service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final String JSON_FILE_PATH = "src/main/resources/Users_mock_data.json";

    @PostConstruct
    public List<UserDto> loadUsers() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream(JSON_FILE_PATH);
            return objectMapper.readValue(inputStream, new TypeReference<List<UserDto>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Users");
        }
    }
    @PreDestroy
    public void saveUsers(List<UserDto> users) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_FILE_PATH), users);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save Users");
        }
    }


    @Override
    public List<UserDto> getUsers() {
        return loadUsers();
    }

    @Override
    public UserDto getUser(Long id) {
        List<UserDto> users = loadUsers();
        for (UserDto user : users) {
            if(user.getId()==id){
                return user;
            }
        }
        throw new RuntimeException("User does not exist in PMS");
    }


    @Override
    public UserDto addUser(UserDto userDto) {
        List<UserDto> users = loadUsers();
        users.add(userDto);
        saveUsers(users);
        return userDto;
    }

    @Override
    public void deleteUser(Long id) {
        List<UserDto> users = loadUsers();
        for(UserDto userDto : users) {
            if(userDto.getId() == id) {
                users.remove(userDto);
            }
            break;
        }
        saveUsers(users);
    }
    @Override
    public void updateUser(Long id, UserDto userDto) {
        if(getUser(id)!=null) {
            List<UserDto> users = loadUsers();
            for(UserDto user : users) {
                if(user.getId() == id) {
                    user=userDto;
                }
                break;
            }
            saveUsers(users);
        }else{
            throw new RuntimeException("User does not exist in PMS");
        }
    }

}
