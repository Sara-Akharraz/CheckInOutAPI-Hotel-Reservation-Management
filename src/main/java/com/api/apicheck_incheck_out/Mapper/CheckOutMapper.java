package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.Dto.CheckOutDTO;
import com.api.apicheck_incheck_out.Dto.UserDto;
import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CheckOutMapper {
    Check_Out toEntity(CheckOutDTO dto);
    CheckOutDTO toDTO(Check_Out entity);
}
