package com.api.apicheck_incheck_out.controllertest;


import com.api.apicheck_incheck_out.controller.UserController;
import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;
    List<UserDto> users = new ArrayList<>();
    UserDto userRequest, user2, userResponse;
    @BeforeEach
    void setUp(){
        userRequest = UserDto.builder()
                .id(1L)
                .cin("AB1111")
                .nom("Alami")
                .email("alami@gmail.com")
                .role(Role.CLIENT)
                .prenom("Khadija")
                .password("alami123")
                .build();
        userResponse = UserDto.builder()
                .id(1L)
                .cin("AB1111")
                .nom("Alami")
                .email("alami@gmail.com")
                .role(Role.CLIENT)
                .prenom("Khadija")
                .password("alami123")
                .build();

        user2 = UserDto.builder()
                .id(3L)
                .cin("AD1111")
                .nom("Ali")
                .email("ali@gmail.com")
                .role(Role.CLIENT)
                .prenom("Ayoub")
                .password("Ali123")
                .build();
    }


    @Test
    void registerTest() throws Exception{
        when(userService.register(any(UserDto.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userRequest.getId()))
                .andExpect(jsonPath("$.nom").value(userResponse.getNom()));

        verify(userService, times(1)).register(any(UserDto.class));
    }

    @Test
    void loginTest() throws Exception{

        String token = "JWT-token";
        when(userService.verify(any(UserDto.class))).thenReturn(token);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(userService, times(1)).verify(any(UserDto.class));
    }

    @Test
    void getUserTest() throws Exception{

        when(userService.getUser(userRequest.getId())).thenReturn(userResponse);

        mockMvc.perform(get("/api/user/{id}",userRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.nom").value(userResponse.getNom()))
                .andExpect(jsonPath("$.prenom").value(userResponse.getPrenom()));

        verify(userService, times(1)).getUser(userRequest.getId());
    }

    @Test
    void getAllUsersTest() throws Exception{
        users.add(userRequest);
        users.add(userResponse);
        users.add(user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userRequest.getId()))
                .andExpect(jsonPath("$[0].nom").value(userRequest.getNom()))
                .andExpect(jsonPath("$[0].prenom").value(userRequest.getPrenom()))
                .andExpect(jsonPath("$[1].id").value(userResponse.getId()))
                .andExpect(jsonPath("$[1].nom").value(userResponse.getNom()))
                .andExpect(jsonPath("$[1].prenom").value(userResponse.getPrenom()))
                .andExpect(jsonPath("$[2].id").value(user2.getId()))
                .andExpect(jsonPath("$[2].nom").value(user2.getNom()))
                .andExpect(jsonPath("$[2].prenom").value(user2.getPrenom()));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getClients() throws Exception{
        users.add(userRequest);
        users.add(userResponse);
        users.add(user2);

        when(userService.getClients()).thenReturn(users);

        mockMvc.perform(get("/api/user/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userRequest.getId()))
                .andExpect(jsonPath("$[0].nom").value(userRequest.getNom()))
                .andExpect(jsonPath("$[0].prenom").value(userRequest.getPrenom()))
                .andExpect(jsonPath("$[1].id").value(userResponse.getId()))
                .andExpect(jsonPath("$[1].nom").value(userResponse.getNom()))
                .andExpect(jsonPath("$[1].prenom").value(userResponse.getPrenom()))
                .andExpect(jsonPath("$[2].id").value(user2.getId()))
                .andExpect(jsonPath("$[2].nom").value(user2.getNom()))
                .andExpect(jsonPath("$[2].prenom").value(user2.getPrenom()));

        verify(userService, times(1)).getClients();
    }

    @Test
    void getReceptionists() throws Exception{
        users.add(userRequest);
        users.add(userResponse);
        users.add(user2);

        when(userService.getReceptionists()).thenReturn(users);

        mockMvc.perform(get("/api/user/receptionists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userRequest.getId()))
                .andExpect(jsonPath("$[0].nom").value(userRequest.getNom()))
                .andExpect(jsonPath("$[0].prenom").value(userRequest.getPrenom()))
                .andExpect(jsonPath("$[1].id").value(userResponse.getId()))
                .andExpect(jsonPath("$[1].nom").value(userResponse.getNom()))
                .andExpect(jsonPath("$[1].prenom").value(userResponse.getPrenom()))
                .andExpect(jsonPath("$[2].id").value(user2.getId()))
                .andExpect(jsonPath("$[2].nom").value(user2.getNom()))
                .andExpect(jsonPath("$[2].prenom").value(user2.getPrenom()));

        verify(userService, times(1)).getReceptionists();
    }

    @Test
    void deleteUser() throws Exception{
        mockMvc.perform(delete("/api/user/{id}",userRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(userRequest.getId());
    }

    @Test
    void updateUser() throws Exception{
        user2.setId(1L);
        when(userService.updateUser(userRequest.getId(),user2)).thenReturn(user2);
        mockMvc.perform(put("/api/user/{id}",userRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user2.getId()))
                .andExpect(jsonPath("$.nom").value(user2.getNom()))
                .andExpect(jsonPath("$.prenom").value(user2.getPrenom()));

        verify(userService, times(1)).updateUser(userRequest.getId(),user2);
    }

}