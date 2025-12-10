package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.ChambreController;
import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.service.ChambreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ChambreControllerTest {
    @Mock
    ChambreService chambreService;
    @InjectMocks
    ChambreController chambreController;
    @Mock
    ChambreMapper chambreMapper;

    @Test
    void testAddChambre(){
        ChambreDTO chambreDTO=new ChambreDTO();
        chambreDTO.setId(1L);

        Chambre chambre=new Chambre();
        chambre.setId(1L);

        when(chambreMapper.toEntity(chambreDTO)).thenReturn(chambre);
        when(chambreService.addChambre(chambre)).thenReturn(chambre);
        when(chambreMapper.toDTO(chambre)).thenReturn(chambreDTO);

        ResponseEntity<ChambreDTO> response=chambreController.addChambre(chambreDTO);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(chambreDTO,response.getBody());
    }

    @Test
    void testUpdateChambre(){
        Long id=1L;

        ChambreDTO chambreDTO=new ChambreDTO();
        chambreDTO.setId(1L);

        Chambre chambre=new Chambre();
        chambre.setId(1L);

        when(chambreMapper.toEntity(chambreDTO)).thenReturn(chambre);
        when(chambreService.updateChambre(id,chambre)).thenReturn(chambre);
        when(chambreMapper.toDTO(chambre)).thenReturn(chambreDTO);

        ResponseEntity<ChambreDTO> response=chambreController.updateChambre(id,chambreDTO);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(chambreDTO,response.getBody());

    }
    @Test
    void testDeleleteChambre(){
        Long id=1L;
        doNothing().when(chambreService).deleteChambre(id);
        ResponseEntity<Void> response=chambreController.deleteChambre(id);
        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
    }

    @Test
    void testGetChambreById(){
        Long id=1L;

        ChambreDTO chambreDTO=new ChambreDTO();
        chambreDTO.setId(1L);

        Chambre chambre=new Chambre();
        chambre.setId(1L);

        when(chambreService.getChambre(id)).thenReturn(chambre);
        when(chambreMapper.toDTO(chambre)).thenReturn(chambreDTO);

        ResponseEntity<ChambreDTO> response=chambreController.getChambreById(id);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(chambreDTO,response.getBody());

    }

    @Test
    void testGetAllChambres(){
        Chambre c1=new Chambre();
        c1.setId(1L);
        Chambre c2=new Chambre();
        c2.setId(2L);

        ChambreDTO dto1=new ChambreDTO();
        dto1.setId(1L);
        ChambreDTO dto2=new ChambreDTO();
        dto2.setId(2L);

        List<Chambre> chambres=List.of(c1,c2);
        List<ChambreDTO> chambreDTOS=List.of(dto1,dto2);

        when(chambreService.getChambres()).thenReturn(chambres);
        when(chambreMapper.toDTO(c1)).thenReturn(dto1);
        when(chambreMapper.toDTO(c2)).thenReturn(dto2);

        ResponseEntity<List<ChambreDTO>> response=chambreController.getAllChambres();
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(response.getBody(),chambreDTOS);
    }
    @Test
    void testGetAllChambreTypes(){
        List<String> expectedTypes = Arrays.stream(ChambreType.values())
                .map(Enum::name)
                .toList();

        ResponseEntity<List<String>> response = chambreController.getAllChambreTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTypes, response.getBody());
    }
}
