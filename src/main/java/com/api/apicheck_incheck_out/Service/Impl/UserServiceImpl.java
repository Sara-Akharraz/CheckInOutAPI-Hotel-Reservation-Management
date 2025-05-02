package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DTO.AuthUser;
import com.api.apicheck_incheck_out.DTO.UserDto;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Mapper.UserMapper;
import com.api.apicheck_incheck_out.Repository.UserRepository;
import com.api.apicheck_incheck_out.Security.JWTService;
import com.api.apicheck_incheck_out.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.boot.CommandLineRunner;
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

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    @Override
    public UserDto logIn(String email, String password) {
        return userRepository.logIn(email, password)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public UserDto register(UserDto userDto) {
        try {
            userDto.setPassword(encoder.encode(userDto.getPassword()));
            User savedUser = userRepository.save(userMapper.toEntity(userDto));
            return userMapper.toDTO(savedUser);
        } catch (Exception e) {
            throw new RuntimeException("Error in registering the user", e);
        }
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
            //userServiceMock.deleteUser(id);
        }else
            throw new RuntimeException("User not found with id: " + id);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        User existingEmailUser = userRepository.findByEmail(user.getEmail());
        if (existingEmailUser!=null && !existingEmailUser.getId().equals(id)) {
            throw new RuntimeException("Email is already taken by another user");
        }
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
        User userEntity = existingUser.get();
        userEntity.setCin(user.getCin());
        userEntity.setEmail(user.getEmail());  // Update email
        userEntity.setNom(user.getNom());
        userEntity.setNumeroPassport(user.getNumeroPassport());
        userEntity.setPassword(user.getPassword());
        userEntity.setPrenom(user.getPrenom());
        userEntity.setRole(user.getRole());
            return userMapper.toDTO(
                    userRepository.save(userEntity));
        }
        else
            throw new RuntimeException("User not found with id: " + id);
    }

    @Override
    public String verify(AuthUser user) {
        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword()
        ));
        if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getEmail());
        else
            return "fail";
    }

}
