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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final AuthenticationManager authManager;


    private final JwtService jwtService;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);




    @Override
    public String getUserNameById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getNom).orElse("Nom inconnu");
    }




    @Override
    public UserDto logIn(String email, String password) {
        return userRepository.logIn(email, password)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

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
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        if(!users.isEmpty())
            return users.stream()
                    .map(userMapper::toDTO)
                    .toList();
        else
            return Collections.emptyList();
    }

    @Override
    public List<UserDto> getReceptionists() {
        List<User> receptionists = userRepository.findReceptionists();

        if (!receptionists.isEmpty()) {
            return receptionists.stream()
                    .map(userMapper::toDTO)
                    .toList();
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
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public void deleteUser(Long id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        }else
            throw new UserNotFoundException("User not found with id: " + id);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        User existingEmailUser = userRepository.findByEmail(user.getEmail());
        if (existingEmailUser!=null && !existingEmailUser.getId().equals(id)) {
            throw new EmailAlreadyUsedException("Email is already taken by another user");
        }
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            User userEntity = existingUser.get();
            userEntity.setCin(user.getCin());
            userEntity.setEmail(user.getEmail());
            userEntity.setNom(user.getNom());
            userEntity.setPassword(encoder.encode(user.getPassword()));
            userEntity.setPrenom(user.getPrenom());
            userEntity.setRole(user.getRole());
            userEntity.setTelephone(user.getTelephone());
            return userMapper.toDTO(
                    userRepository.save(userEntity));
        }
        else
            throw new UserNotFoundException("User not found with id: " + id);
    }

    @Override
    public List<User> getAdmins() {
        return userRepository.findAdmins();
    }

    @Override
    public String verify(UserDto user) {
        Authentication authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        user.getEmail(), user.getPassword()
                ));
        if(authentication.isAuthenticated()) {
            User authenticatedUser = userRepository.findByEmail(authentication.getName());
            return jwtService.generateToken(authenticatedUser);
        }
        else
            return "fail";
    }

}
