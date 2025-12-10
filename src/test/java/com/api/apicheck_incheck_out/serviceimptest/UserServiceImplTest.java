package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.exceptionhandling.EmailAlreadyUsedException;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.UserRegistrationException;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.factory.UserModifier;
import com.api.apicheck_incheck_out.service.factory.UserTypesFinder;
import com.api.apicheck_incheck_out.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    UserMapper userMapper;
    @Mock
    UserTypesFinder userTypesFinder;
    @Mock
    private JwtService jwtService;
    @Mock
    UserModifier userModifier;
    @InjectMocks
    UserServiceImpl userService;
    private User user, user1, user2;
    List<User> users = new ArrayList<>();
    UserDto dtoMock = new UserDto();



    @BeforeEach
    void setUp(){
        user = User.builder()
                .id(1L)
                .cin("AB1111")
                .nom("Alami")
                .email("alami@gmail.com")
                .role(Role.CLIENT)
                .prenom("Khadija")
                .password("alami123")
                .build();
        user1 = User.builder()
                .id(2L)
                .cin("AC1111")
                .nom("Moujahid")
                .email("moujahid@gmail.com")
                .role(Role.ADMIN)
                .prenom("Salma")
                .password("salma123")
                .build();

        user2 = User.builder()
                .id(3L)
                .cin("AD1111")
                .nom("Ali")
                .email("ali@gmail.com")
                .role(Role.CLIENT)
                .prenom("Ayoub")
                .password("Ali123")
                .build();

        dtoMock.setId(1L);
        dtoMock.setNom("Alami");
        dtoMock.setEmail("alami@gmail.com");
        dtoMock.setPassword("alami123");

    }

    @Test
    void getUserTest(){
        when(userTypesFinder.findById(1L)).thenReturn(dtoMock);
        UserDto foundedUser = userService.getUser(1L);

        assertNotNull(foundedUser);
        assertEquals(user.getId(),foundedUser.getId());
        assertEquals(user.getEmail(),foundedUser.getEmail());
    }


    @Test
    void getAllUsersTest_NoUser(){
        when(userTypesFinder.findUsers()).thenReturn(Collections.emptyList());

        List<UserDto> foundedUsers = userService.getAllUsers();

        assertNotNull(foundedUsers);
        assertTrue(foundedUsers.isEmpty());
    }


    @Test
    void getReceptionists_NoReceptionist(){
        when(userTypesFinder.findReceptionists()).thenReturn(Collections.emptyList());

        List<UserDto> foundedReceptionists = userService.getReceptionists();

        assertNotNull(foundedReceptionists);
        assertTrue(foundedReceptionists.isEmpty());
    }


    @Test
    void getClients_NoClient(){
        when(userTypesFinder.findClients()).thenReturn(Collections.emptyList());

        List<UserDto> foundedClients = userService.getClients();

        assertNotNull(foundedClients);
        assertTrue(foundedClients.isEmpty());
    }

    @Test
    void getAdmins(){

        users.add(user1);
        users.add(user2);

        when(userTypesFinder.findAdmins()).thenReturn(users);

        List<User> foundedAdmins = userService.getAdmins();

        assertEquals(2,foundedAdmins.size());
        assertEquals(user1.getId(),foundedAdmins.get(0).getId());
        assertEquals(user2.getId(),foundedAdmins.get(1).getId());
        assertEquals(user1.getEmail(),foundedAdmins.get(0).getEmail());
        assertEquals(user2.getEmail(),foundedAdmins.get(1).getEmail());
    }

    @Test
    void deleteUser(){
        doNothing().when(userTypesFinder).verifyPresenece(1L);
        userService.deleteUser(1L);
        Mockito.verify(userTypesFinder, times(1)).verifyPresenece(1L);
    }


    @Test
    void verifyTest() {
        Authentication authenticationMock = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);
        when(authenticationMock.getName()).thenReturn(dtoMock.getEmail());

        when(userRepository.findByEmail(dtoMock.getEmail())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        String result = userService.verify(dtoMock);

        assertEquals("jwt-token", result);
        Mockito.verify(jwtService, times(1)).generateToken(user);

    }

    @Test
    void verifyTest_ReturnFail(){
        Authentication authenticationMock = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(false);

        String result = userService.verify(dtoMock);

        assertEquals("fail", result);

    }

    @Test
    void registerTest(){
        when(userMapper.toEntity(dtoMock)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(dtoMock);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto savedUser = userService.register(dtoMock);

        assertNotNull(savedUser);
        assertEquals(1L, savedUser.getId());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getTelephone(), savedUser.getTelephone());

        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toEntity(dtoMock);
        Mockito.verify(userMapper).toDTO(user);
    }

    @Test
    void testRegister_Exception() {

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        UserRegistrationException e = assertThrows(UserRegistrationException.class,
                () -> userService.register(dtoMock));

        assertEquals("Error in registering the user", e.getMessage());
    }
}