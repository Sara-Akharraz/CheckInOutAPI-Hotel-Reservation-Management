package com.example.demo.Mapper;

import com.example.demo.Dto.UserDto;
import com.example.demo.Entity.User;
import org.mapstruct.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto dto);
    UserDto toDTO(User entity);
}
