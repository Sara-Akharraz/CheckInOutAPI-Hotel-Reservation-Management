package com.api.apicheck_incheck_out.mappertest;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.Role;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // given
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setNom("alami");
        dto.setPrenom("ali");
        dto.setEmail("alami@gmail.com");
        dto.setPassword("psswd");
        dto.setRole(Role.CLIENT);
        dto.setTelephone("0600000000");
        dto.setNotifications(List.of(new Notification()));
        dto.setCin("AB12345");
        dto.setNumeroPassport("P123456");

        // when
        User entity = userMapper.toEntity(dto);

        // then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getNom(), entity.getNom());
        assertEquals(dto.getPrenom(), entity.getPrenom());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getPassword(), entity.getPassword());
        assertEquals(dto.getRole(), entity.getRole());
        assertEquals(dto.getTelephone(), entity.getTelephone());
        assertEquals(dto.getCin(), entity.getCin());
        assertEquals(dto.getNumeroPassport(), entity.getNumeroPassport());
        assertEquals(dto.getNotifications().size(), entity.getNotifications().size());
    }

    @Test
    void toDTO_shouldMapEntityToDto() {

        User entity = User.builder()
                .id(1L)
                .nom("alami")
                .prenom("ali")
                .email("alami@gmail.com")
                .password("psswd")
                .role(Role.CLIENT)
                .telephone("0600000000")
                .notifications(List.of(new Notification()))
                .cin("AB12345")
                .numeroPassport("P123456")
                .build();

        UserDto dto = userMapper.toDTO(entity);


        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getNom(), dto.getNom());
        assertEquals(entity.getPrenom(), dto.getPrenom());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getPassword(), dto.getPassword());
        assertEquals(entity.getRole(), dto.getRole());
        assertEquals(entity.getTelephone(), dto.getTelephone());
        assertEquals(entity.getCin(), dto.getCin());
        assertEquals(entity.getNumeroPassport(), dto.getNumeroPassport());
        assertEquals(entity.getNotifications().size(), dto.getNotifications().size());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(userMapper.toEntity(null));
    }

    @Test
    void toDTO_shouldReturnNull_whenEntityIsNull() {
        assertNull(userMapper.toDTO(null));
    }
}
