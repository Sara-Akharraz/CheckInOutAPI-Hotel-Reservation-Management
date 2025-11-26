package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.security.JwtService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private JwtService jwtService;
    private User user, user1, user2;
    List<User> users = new ArrayList<>();

    UserDto dtoMock = new UserDto();

    @InjectMocks
    UserServiceImpl userService;

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



        //when(userMapper.toEntity(dtoMock)).thenReturn(user);
        //when(userMapper.toEntity(dtoMock1)).thenReturn(user1);


    }

    @Test
    void getUserTest(){
        when(userMapper.toDTO(user)).thenReturn(dtoMock);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        UserDto foundedUser = userService.getUser(1L);

        assertNotNull(foundedUser);
        assertEquals(user.getId(),foundedUser.getId());
        assertEquals(user.getEmail(),foundedUser.getEmail());
    }

    @Test
    void getAllUsersTest(){
        UserDto dtoMock1 = new UserDto();
        dtoMock1.setId(2L);
        dtoMock1.setNom("Moujahid");
        dtoMock1.setEmail("moujahid@gmail.com");
        dtoMock1.setPassword("salma123");

        users.add(user);
        users.add(user1);

        when(userMapper.toDTO(user)).thenReturn(dtoMock);
        when(userMapper.toDTO(user1)).thenReturn(dtoMock1);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> foundedUsers = userService.getAllUsers();
        assertEquals(2,foundedUsers.size());
        assertEquals(user.getId(),foundedUsers.get(0).getId());
        assertEquals(user1.getId(),foundedUsers.get(1).getId());
        assertEquals(user.getEmail(),foundedUsers.get(0).getEmail());
        assertEquals(user1.getEmail(),foundedUsers.get(1).getEmail());

    }

    @Test
    void getReceptionistsTest(){
        UserDto userDto1 = new UserDto();
        userDto1.setId(2L);
        userDto1.setNom("Moujahid");
        userDto1.setRole(Role.RECEPTIONIST);
        userDto1.setEmail("moujahid@gmail.com");
        userDto1.setPassword("salma123");
        users.add(user1);

        UserDto userDto2 = new UserDto();
        userDto2.setId(3L);
        userDto2.setNom("Ali");
        userDto2.setRole(Role.RECEPTIONIST);
        userDto2.setEmail("ali@gmail.com");
        userDto2.setPassword("ali123");

        users.add(user2);

        when(userMapper.toDTO(user1)).thenReturn(userDto1);
        when(userMapper.toDTO(user2)).thenReturn(userDto2);
        when(userRepository.findReceptionists()).thenReturn(users);

        List<UserDto> foundedReceptionists = userService.getReceptionists();

        List<UserDto> foundedUsers = userService.getAllUsers();
        assertEquals(2,foundedReceptionists.size());
        assertEquals(user1.getId(),foundedReceptionists.get(0).getId());
        assertEquals(user2.getId(),foundedReceptionists.get(1).getId());
        assertEquals(user1.getEmail(),foundedReceptionists.get(0).getEmail());
        assertEquals(user2.getEmail(),foundedReceptionists.get(1).getEmail());
    }

    @Test
    void getClients(){
        UserDto userDto1 = new UserDto();
        userDto1.setId(2L);
        userDto1.setNom("Moujahid");
        userDto1.setRole(Role.CLIENT);
        userDto1.setEmail("moujahid@gmail.com");
        userDto1.setPassword("salma123");
        users.add(user1);

        UserDto userDto2 = new UserDto();
        userDto2.setId(3L);
        userDto2.setNom("Ali");
        userDto2.setRole(Role.CLIENT);
        userDto2.setEmail("ali@gmail.com");
        userDto2.setPassword("ali123");

        users.add(user2);

        when(userMapper.toDTO(user1)).thenReturn(userDto1);
        when(userMapper.toDTO(user2)).thenReturn(userDto2);
        when(userRepository.findReceptionists()).thenReturn(users);

        List<UserDto> foundedReceptionists = userService.getReceptionists();

        assertEquals(2,foundedReceptionists.size());
        assertEquals(user1.getId(),foundedReceptionists.get(0).getId());
        assertEquals(user2.getId(),foundedReceptionists.get(1).getId());
        assertEquals(user1.getEmail(),foundedReceptionists.get(0).getEmail());
        assertEquals(user2.getEmail(),foundedReceptionists.get(1).getEmail());
    }


    @Test
    void deleteUser(){
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        userService.deleteUser(1L);
        Mockito.verify(userRepository, times(1)).deleteById(1L);
    }


    @Test
    void updateUserTest() {

        // DTO for update
        UserDto dtoMock = new UserDto();
        dtoMock.setId(1L);
        dtoMock.setEmail("alami-salma22@gmail.com");
        dtoMock.setNom("AlamiUpdated");
        dtoMock.setPrenom("KhadijaUpdated");
        dtoMock.setPassword("alami123");
        dtoMock.setTelephone("0611111111");

        // New user after save
        User newUserInfo = User.builder()
                .id(1L)
                .cin("AB1111")
                .nom(dtoMock.getNom())
                .email(dtoMock.getEmail())
                .role(Role.CLIENT)
                .prenom(dtoMock.getPrenom())
                .password("alami-salma-2003")
                .telephone(dtoMock.getTelephone())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(newUserInfo);
        when(userMapper.toDTO(any(User.class))).thenReturn(dtoMock);

        UserDto updatedUser = userService.updateUser(user.getId(), dtoMock);

        assertNotNull(updatedUser);
        assertEquals(newUserInfo.getId(), updatedUser.getId());
        assertEquals(newUserInfo.getEmail(), updatedUser.getEmail());
        assertEquals(newUserInfo.getTelephone(), updatedUser.getTelephone());

    }

    @Test
    void verifyTest() {
        Authentication authenticationMock = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);
        when(authenticationMock.getName()).thenReturn(dtoMock.getEmail() );

        when(userRepository.findByEmail(dtoMock.getEmail())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        String result = userService.verify(dtoMock);

        assertEquals("jwt-token", result);
        Mockito.verify(jwtService, times(1)).generateToken(user);

    }

    @Test
    void registerTest(){
        when(userMapper.toEntity(dtoMock)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(dtoMock);
        //when(encoder.encode(anyString())).thenReturn("encoded_psswd");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto savedUser = userService.register(dtoMock);

        assertNotNull(savedUser);
        assertEquals(1L, savedUser.getId());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getTelephone(), savedUser.getTelephone());

        //Mockito.verify(encoder).encode(dtoMock.getPassword());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toEntity(dtoMock);
        Mockito.verify(userMapper).toDTO(user);
    }

}
