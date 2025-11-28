package com.api.apicheck_incheck_out.mappertest;

import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreReservationNotFoundException;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ChambreMapperTest {
 @Mock
    private ChambreReservationRepository chambreReservationRepository;
 @Mock
    private ChambreMapper chambreMapper;
    @BeforeEach
    void setUp() {
        chambreMapper = new ChambreMapper(chambreReservationRepository);
    }
    @Test
    void testTODTO(){
        ChambreReservation cr1 = new ChambreReservation();
        cr1.setId(10L);
        ChambreReservation cr2 = new ChambreReservation();
        cr2.setId(20L);

        Chambre chambre=Chambre.builder()
                .id(1L)
                .nom("chambre 104")
                .etage("1")
                .prix(100.0)
                .type(ChambreType.DOUBLE)
                .capacite(3)
                .chambreReservations(List.of(cr1,cr2))
                .build();

        ChambreDTO dto=chambreMapper.toDTO(chambre);

        assertEquals(chambre.getId(), dto.getId());
        assertEquals(chambre.getNom(), dto.getNom());
        assertEquals(chambre.getEtage(), dto.getEtage());
        assertEquals(chambre.getPrix(), dto.getPrix());
        assertEquals(chambre.getType(), dto.getType());
        assertEquals(chambre.getCapacite(), dto.getCapacite());
        assertEquals(List.of(10L, 20L), dto.getChambreReservationIds());
    }
    @Test
    void testToEntity(){
        ChambreDTO dto=new ChambreDTO();
        dto.setId(1L);
        dto.setNom("chambre 105");
        dto.setEtage("2");
        dto.setPrix(150.0);
        dto.setType(ChambreType.DOUBLE);
        dto.setCapacite(4);
        dto.setChambreReservationIds(List.of(10L, 20L));

        ChambreReservation cr1 = new ChambreReservation();
        cr1.setId(10L);
        ChambreReservation cr2 = new ChambreReservation();
        cr2.setId(20L);
        when(chambreReservationRepository.findById(10L)).thenReturn(Optional.of(cr1));
        when(chambreReservationRepository.findById(20L)).thenReturn(Optional.of(cr2));

        Chambre chambre=chambreMapper.toEntity(dto);
        assertEquals(dto.getId(), chambre.getId());
        assertEquals(dto.getNom(), chambre.getNom());
        assertEquals(dto.getEtage(), chambre.getEtage());
        assertEquals(dto.getPrix(), chambre.getPrix());
        assertEquals(dto.getType(), chambre.getType());
        assertEquals(dto.getCapacite(), chambre.getCapacite());
        assertEquals(List.of(cr1, cr2), chambre.getChambreReservations());

    }
    @Test
    void testToEntity_ChambreReservationNotFound(){
        ChambreDTO dto=new ChambreDTO();
        dto.setChambreReservationIds(List.of(1L));
        when(chambreReservationRepository.findById(1L)).thenReturn(Optional.empty());

        ChambreReservationNotFoundException ex=assertThrows(ChambreReservationNotFoundException.class,()->chambreMapper.toEntity(dto));
        assertEquals("ChambreReservation non trouvée avec l'id : 1", ex.getMessage());
    }
}
