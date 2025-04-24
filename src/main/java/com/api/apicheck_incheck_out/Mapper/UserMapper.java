package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.UserDto;
import com.api.apicheck_incheck_out.Entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto dto);
    UserDto toDTO(User entity);
    List<User> toEntityList(List<UserDto> dtoList);

    List<UserDto> toDTOList(List<User> entityList);
}
