package com.api.apicheck_incheck_out.PMSMock.Service.Impl;

import com.api.apicheck_incheck_out.Dto.UserDto;
import com.api.apicheck_incheck_out.PMSMock.Model.ChambreModel;
import com.api.apicheck_incheck_out.PMSMock.Service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final String JSON_FILE_PATH = "/User_mock_data.json";
    private List<UserDto> users;
    @PostConstruct
    public void loadUsers() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream(JSON_FILE_PATH);
            this.users = objectMapper.readValue(inputStream, new TypeReference<List<UserDto>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Users");
        }
    }

    // PreDestroy method to save users without parameters
    @PreDestroy
    public void saveUsers() {
        try {
            if (this.users != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_FILE_PATH), this.users);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save Users");
        }
    }


    @Override
    public List<UserDto> getUsers() {
        return users;
    }

    @Override
    public UserDto getUser(Long id) {
        for (UserDto user : users) {
            if(user.getId()==id){
                return user;
            }
        }
        throw new RuntimeException("User does not exist in PMS");
    }


    @Override
    public UserDto addUser(UserDto userDto) {
        users.add(userDto);
        saveUsers();
        return userDto;
    }

    @Override
    public void deleteUser(Long id) {
        for(UserDto userDto : users) {
            if(userDto.getId() == id) {
                users.remove(userDto);
            }
            break;
        }
        saveUsers();
    }
    @Override
    public void updateUser(Long id, UserDto userDto) {
        if(getUser(id)!=null) {
            for(UserDto user : users) {
                if(user.getId() == id) {
                    user=userDto;
                }
                break;
            }
            saveUsers();
        }else{
            throw new RuntimeException("User does not exist in PMS");
        }
    }

}
