package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.exceptionhandling.EmailAlreadyUsedException;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserModifierTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    UserTypesFinder userTypesFinder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    UserModifier userModifier;
    UserDto dtoMock;


    @BeforeEach
    void setUp(){
        dtoMock = UserDto.builder()
                .id(2L)
                .cin("AC1111")
                .nom("Moujahid")
                .email("moujahid@gmail.com")
                .role(Role.ADMIN)
                .prenom("Salma")
                .password("salma123")
                .build();

    }

    @Test
    void updateUserTest() {

        dtoMock.setId(1L);
        dtoMock.setEmail("alami-salma22@gmail.com");
        dtoMock.setNom("AlamiUpdated");
        dtoMock.setPrenom("KhadijaUpdated");
        dtoMock.setPassword("alami123");
        dtoMock.setTelephone("0611111111");
        dtoMock.setCin("AB12345");

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

        when(userTypesFinder.verifyExistance(1L,dtoMock)).thenReturn(true);
        when(userTypesFinder.findById(1L)).thenReturn(dtoMock);

        UserDto updatedUser = userModifier.modify(1L, dtoMock);

        assertNotNull(updatedUser);
        assertEquals(newUserInfo.getId(), updatedUser.getId());
        assertEquals(newUserInfo.getEmail(), updatedUser.getEmail());
        assertEquals(newUserInfo.getTelephone(), updatedUser.getTelephone());
    }

    @Test
    void updateUserTest_EmailAlreadyUsedException(){
        doThrow(new EmailAlreadyUsedException("Email is already taken by another user"))
                .when(userTypesFinder)
                .verifyExistance(1L, dtoMock);

        EmailAlreadyUsedException e = assertThrows(EmailAlreadyUsedException.class,
                () -> userModifier.modify(1L,dtoMock));

        assertEquals("Email is already taken by another user", e.getMessage());

    }
}
