package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Dto.UserDto;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Mapper.UserMapper;
import com.api.apicheck_incheck_out.Repository.UserRepository;
import com.api.apicheck_incheck_out.Service.UserServiceMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceMockImpl implements UserServiceMock {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    UserRepository userRepository;

    private com.api.apicheck_incheck_out.PMSMock.Service.UserService userServiceMock;

    public void loadUsersToDatabase(List<User> users) {
        if (userRepository.count() == 0) { // Ensure it runs only once
            userRepository.saveAll(users);
            System.out.println("Users loaded into the database.");
        } else {
            System.out.println("Users already exist in the database. Skipping load.");
        }
    }

    @Override
    public UserDto logIn(String email, String password) {
        return userRepository.logIn(email, password)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }


    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if(!users.isEmpty())
            return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        else
            return Collections.emptyList();
    }

    @Override
    public List<UserDto> getReceptionists() {
        List<User> receptionists = userRepository.findReceptionists();

        if (!receptionists.isEmpty()) {
            return receptionists.stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public List<UserDto> getClients() {
        List<User> users = userRepository.findClients();

        if (!users.isEmpty()) {
            return users.stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public void deleteUser(Long id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            userServiceMock.deleteUser(id);
        }else
            throw new RuntimeException("User not found with id: " + id);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userServiceMock.updateUser(id, user);
            return userMapper.toDTO(
                    userRepository.save(userMapper.toEntity(user)));
        }
        else
            throw new RuntimeException("User not found with id: " + id);
    }
}
