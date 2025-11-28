package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.UserRegistrationException;
import com.api.apicheck_incheck_out.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto user){
            return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDto user){
        return userService.verify(user);

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id){
            return ResponseEntity.ok(userService.getUser(id));
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
            return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/receptionists")
    public ResponseEntity<List<UserDto>> getReceptionists(){
            return ResponseEntity.ok(userService.getReceptionists());
    }

    @GetMapping("/clients")
    public ResponseEntity<List<UserDto>> getClients(){
        return ResponseEntity.ok(userService.getClients());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id){
            userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id,@RequestBody UserDto user){
            return ResponseEntity.ok(userService.updateUser(id, user));

    }
}