package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.ChambreReservationController;
import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.service.ChambreReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ChambreReservationControllerTest {
    @Mock
    private ChambreReservationService chambreReservationService;

    @Mock
    private ChambreMapper chambreMapper;

    @InjectMocks
    private ChambreReservationController chambreReservationController;

    @Test
    void testGetChambresDisponibles(){
        List<Chambre> chambres = List.of(new Chambre(), new Chambre());
        List<ChambreDTO> dtos = List.of(new ChambreDTO(), new ChambreDTO());

        when(chambreReservationService.getChambresDisponibles()).thenReturn(chambres);
        when(chambreMapper.toDTO(chambres.get(0))).thenReturn(dtos.get(0));
        when(chambreMapper.toDTO(chambres.get(1))).thenReturn(dtos.get(1));

        ResponseEntity<List<ChambreDTO>> response = chambreReservationController.getChambresDisponibles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());

    }
    @Test
    void testSetChambreOccupee(){
        Long id = 1L;
        doNothing().when(chambreReservationService).setChambreOccupee(id);

        ResponseEntity<Void> response = chambreReservationController.setChambreOccupee(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    @Test
    void testSetChambreDisponibles(){
        Long id=1L;
        doNothing().when(chambreReservationService).setChambreDisponible(id);

        ResponseEntity<Void> response=chambreReservationController.setChambreDisponible(id);
        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
    }
    @Test
    void testGetChambresByReservation(){
        Long reservationId = 1L;
        List<Chambre> chambres = List.of(new Chambre());
        List<ChambreDTO> dtos = List.of(new ChambreDTO());

        when(chambreReservationService.getChambresByReservation(reservationId)).thenReturn(chambres);
        when(chambreMapper.toDTO(chambres.get(0))).thenReturn(dtos.get(0));

        ResponseEntity<List<ChambreDTO>> response = chambreReservationController.getChambresByReservation(reservationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }
    @Test
    void testSetChambreReserved(){
        Long idchambre=1L;
        Long idreservation=1L;
        doNothing().when(chambreReservationService).setChambreReserved(idchambre,idreservation);
        ResponseEntity<Void> response=chambreReservationController.setChambreReserved(idchambre,idreservation);
        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
    }
    @Test
    void testGetChambresDisponiblesAvecFilter(){
        String dateDebut = "2025-12-01";
        String dateFin = "2025-12-05";
        Integer capacite = 2;
        ChambreType type = ChambreType.SINGLE;
        String etage = "1";

        Chambre chambre1 = new Chambre();
        Chambre chambre2 = new Chambre();

        ChambreDTO dto1 = new ChambreDTO();
        ChambreDTO dto2 = new ChambreDTO();

        List<Chambre> chambres = List.of(chambre1, chambre2);
        List<ChambreDTO> dtos = List.of(dto1, dto2);


        when(chambreReservationService.findChambresDisponibles(dateDebut, dateFin, capacite, type, etage))
                .thenReturn(chambres);
        when(chambreMapper.toDTO(chambre1)).thenReturn(dto1);
        when(chambreMapper.toDTO(chambre2)).thenReturn(dto2);

        ResponseEntity<List<ChambreDTO>> response =
                chambreReservationController.getChambresDisponiblesAvecFiltre(dateDebut, dateFin, capacite, type, etage);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }
}
