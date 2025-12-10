package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.exceptionhandling.EmailAlreadyUsedException;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.UserRegistrationException;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.UserService;
import com.api.apicheck_incheck_out.service.factory.UserModifier;
import com.api.apicheck_incheck_out.service.factory.UserTypesFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    private final UserTypesFinder userTypesFinder;
    @Autowired
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private final UserModifier userModifier;


    @Override
    public UserDto register(UserDto userDto) {
        try {
            userDto.setPassword(encoder.encode(userDto.getPassword()));
            User savedUser = userRepository.save(userMapper.toEntity(userDto));
            return userMapper.toDTO(savedUser);
        } catch (Exception e) {
            throw new UserRegistrationException("Error in registering the user", e);
        }
    }


    @Override
    public UserDto getUser(Long id) {
        return userTypesFinder.findById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userTypesFinder.findUsers();
    }

    @Override
    public List<UserDto> getReceptionists() {
            return userTypesFinder.findReceptionists();
    }


    @Override
    public List<UserDto> getClients() {
            return userTypesFinder.findClients();
    }


    @Override
    public void deleteUser(Long id) {
        userTypesFinder.verifyPresenece(id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        return userModifier.modify(id, user);
    }

    @Override
    public List<User> getAdmins() {
        return userTypesFinder.findAdmins();
    }

    @Override
    public String verify(UserDto user) {
        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        user.getEmail(), user.getPassword()
                ));
        if (!authentication.isAuthenticated()) {
            return "fail";
        }
        User authenticatedUser = userRepository.findByEmail(authentication.getName());
        return jwtService.generateToken(authenticatedUser);
    }

}
