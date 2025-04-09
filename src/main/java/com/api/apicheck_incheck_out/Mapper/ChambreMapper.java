package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.Dto.ChambreDto;
import com.api.apicheck_incheck_out.Entity.Chambre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChambreMapper {

    Chambre toEntity(ChambreDto dto);
    ChambreDto toDTO(Chambre entity);
}
