package com.api.apicheck_incheck_out.securitytest;

import com.api.apicheck_incheck_out.dto.UserPricipal;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.repository.UserRepository;
import com.api.apicheck_incheck_out.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


 class CustomUserDetailsTest {

    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }
    @Test
    void loadUserByUserNameTest(){
        User user = new User();
        user.setId(1L);
        user.setEmail("alami@gmail.com");
        user.setPassword("psswd");

        when(userRepository.findByEmail("alami@gmail.com")).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("alami@gmail.com");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof UserPricipal);
        assertEquals("alami@gmail.com", userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail("alami@gmail.com");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        String email = "alami@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        verify(userRepository, times(1)).findByEmail(email);
    }
}
