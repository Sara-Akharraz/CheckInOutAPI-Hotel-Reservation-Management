package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.exceptionhandling.EmailAlreadyUsedException;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import com.api.apicheck_incheck_out.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserTypesFinder {
    UserRepository userRepository;
    UserMapper userMapper;


    public List<UserDto> findUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public List<UserDto> findReceptionists() {
        return userRepository.findReceptionists().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public List<UserDto> findClients() {
        return userRepository.findClients().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public List<User> findAdmins() {
        return userRepository.findAdmins();
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    public boolean verifyExistance(Long id, UserDto user){
        User existingEmailUser = userRepository.findByEmail(user.getEmail());
        boolean isDuplicateEmail = existingEmailUser != null && !existingEmailUser.getId().equals(id);
        if (isDuplicateEmail) {
            throw new EmailAlreadyUsedException("Email is already taken by another user");
        }
        return true;

    }

    public void verifyPresenece(Long id) {
        if (!userRepository.findById(id).isPresent())
            throw new UserNotFoundException("User not found with id: " + id);
    }
}
