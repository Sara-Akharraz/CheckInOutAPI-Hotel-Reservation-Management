package com.example.demo.Service.Impl;

import com.example.demo.Dto.UserDto;
import com.example.demo.Entity.User;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDto logIn(String email, String password) {
        return userRepository.logIn(email, password)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }


    @Override
    public void logOut(Long id) {

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
        List<User> receptionists = UserRepository.findReceptionists();

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
        if(userRepository.findById(id).isPresent())
            userRepository.deleteById(id);

        else
            throw new RuntimeException("User not found with id: " + id);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent())
            return userMapper.toDTO(
                    userRepository.save(userMapper.toEntity(user)));
        else
            throw new RuntimeException("User not found with id: " + id);
    }
}
