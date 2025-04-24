package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.UserDto;
import com.api.apicheck_incheck_out.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto user){
        try{
            return ResponseEntity.ok(userService.register(user));
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDto user){
//        try {
//            return ResponseEntity.ok(userService.logIn(user.getEmail(), user.getPassword()));
//        }catch(Exception e){
//            e.printStackTrace();
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
//        }
        return userService.verify(user);
        //return "success";
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id){
        try{
            return ResponseEntity.ok(userService.getUser(id));
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        try{
            return ResponseEntity.ok(userService.getAllUsers());
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @GetMapping("/receptionists")
    public ResponseEntity<List<UserDto>> getReceptionists(){
        try{
            return ResponseEntity.ok(userService.getReceptionists());
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<List<UserDto>> getClients(){
        try{
            return ResponseEntity.ok(userService.getClients());
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id){
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id,@RequestBody UserDto user){
        try{
            return ResponseEntity.ok(userService.updateUser(id, user));
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
 }

