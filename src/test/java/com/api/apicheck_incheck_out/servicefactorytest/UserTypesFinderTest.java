package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.exceptionhandling.EmailAlreadyUsedException;
import com.api.apicheck_incheck_out.exceptionhandling.UserNotFoundException;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.security.JwtService;
import com.api.apicheck_incheck_out.service.factory.UserModifier;
import com.api.apicheck_incheck_out.service.factory.UserTypesFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserTypesFinderTest {

    @Mock
    UserRepository userRepository;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserTypesFinder userTypesFinder;
    @Mock
    private JwtService jwtService;
    @Mock
    UserModifier userModifier;
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
    void getUserTest_UserNotFoundException(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userTypesFinder.findById(1L));

        assertEquals("User not found with id: 1", e.getMessage());
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

        List<UserDto> foundedUsers = userTypesFinder.findUsers();
        assertEquals(2,foundedUsers.size());
        assertEquals(user.getId(),foundedUsers.get(0).getId());
        assertEquals(user1.getId(),foundedUsers.get(1).getId());
        assertEquals(user.getEmail(),foundedUsers.get(0).getEmail());
        assertEquals(user1.getEmail(),foundedUsers.get(1).getEmail());

    }


    @Test
    void verifyUserTest_UserNotFoundException(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userTypesFinder.findById(1L));

        assertEquals("User not found with id: "+1L, e.getMessage());
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
        when(userRepository.findClients()).thenReturn(users);

        List<UserDto> foundedClients = userTypesFinder.findClients();

        assertEquals(2,foundedClients.size());
        assertEquals(user1.getId(),foundedClients.get(0).getId());
        assertEquals(user2.getId(),foundedClients.get(1).getId());
        assertEquals(user1.getEmail(),foundedClients.get(0).getEmail());
        assertEquals(user2.getEmail(),foundedClients.get(1).getEmail());
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

        List<UserDto> foundedReceptionists = userTypesFinder.findReceptionists();

        assertEquals(2,foundedReceptionists.size());
        assertEquals(user1.getId(),foundedReceptionists.get(0).getId());
        assertEquals(user2.getId(),foundedReceptionists.get(1).getId());
        assertEquals(user1.getEmail(),foundedReceptionists.get(0).getEmail());
        assertEquals(user2.getEmail(),foundedReceptionists.get(1).getEmail());
    }

    @Test
    void getAdminsTest_NoAdmin(){
        when(userRepository.findAdmins()).thenReturn(Collections.emptyList());

        List<User> foundedAdmins = userTypesFinder.findAdmins();

        assertNotNull(foundedAdmins);
        assertTrue(foundedAdmins.isEmpty());
    }

    @Test
    void deleteUserTest_UserNotFoundException(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userTypesFinder.verifyPresenece(1L));

        assertEquals("User not found with id: "+1L, e.getMessage());
    }


}
