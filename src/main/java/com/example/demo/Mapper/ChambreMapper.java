package com.example.demo.Mapper;

import com.example.demo.Dto.ChambreDto;
import com.example.demo.Entity.Chambre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChambreMapper {

    Chambre toEntity(ChambreDto dto);
    ChambreDto toDTO(Chambre entity);
}
