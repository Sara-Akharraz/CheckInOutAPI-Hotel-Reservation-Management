package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.UserDto;
import com.api.apicheck_incheck_out.Entity.Notification;
import com.api.apicheck_incheck_out.Entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User.UserBuilder user = User.builder();


        user.id(dto.getId());
        user.nom(dto.getName());
        user.prenom(dto.getPrenom());
        user.password(dto.getPassword());
        user.email(dto.getEmail());
        user.cin(dto.getCin());
        user.numeroPassport(dto.getNumeroPassport());


        List<Notification> list = dto.getNotifications();
        if (list != null) {
            user.notifications(new ArrayList<>(list));
        }

        return user.build();
    }

    public UserDto toDTO(User entity) {
        if (entity == null) {
            return null;
        }

        UserDto userDto = new UserDto();


        userDto.setId(entity.getId());
        userDto.setName(entity.getNom());
        userDto.setPrenom(entity.getPrenom());
        userDto.setEmail(entity.getEmail());
        userDto.setPassword(entity.getPassword());

        List<Notification> list = entity.getNotifications();
        if (list != null) {
            userDto.setNotifications(new ArrayList<>(list));
        }


        userDto.setCin(entity.getCin());
        userDto.setNumeroPassport(entity.getNumeroPassport());


        return userDto;
    }
}
