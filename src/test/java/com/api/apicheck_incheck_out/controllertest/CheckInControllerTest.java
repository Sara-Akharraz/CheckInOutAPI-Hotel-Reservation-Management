package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.CheckInController;
import com.api.apicheck_incheck_out.dto.ApiResponse;
import com.api.apicheck_incheck_out.dto.CheckInDTO;
import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import com.api.apicheck_incheck_out.mapper.CheckInMapper;
import com.api.apicheck_incheck_out.mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.CheckInService;
import com.api.apicheck_incheck_out.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CheckInControllerTest {
    @Mock
    CheckInService checkInService;
    @InjectMocks
    CheckInController checkInController;
    @Mock
    CheckInMapper checkInMapper;
    @Mock
    DocumentScanMapper documentScanMapper;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReservationService reservationService;
    @Mock
    MultipartFile file;


    @Test
    void testCheckInsForTody(){
        LocalDate date=LocalDate.now();
        CheckIn checkIn=new CheckIn();
        CheckInDTO dto=new CheckInDTO();

        when(checkInService.checkinsForToday(date)).thenReturn(List.of(checkIn));
        when(checkInMapper.toDTO(checkIn)).thenReturn(dto);

        ResponseEntity<List<CheckInDTO>> response=checkInController.checkInsForToday(date);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(List.of(dto),response.getBody());
    }
    @Test
    void testGetDocumentScanByCheckIn(){
        Long id=1L;
        DocumentScan doc=new DocumentScan();
        DocumentScanDTO dto=new DocumentScanDTO();

        when(checkInService.getDocumentByCheckin(id)).thenReturn(doc);
        when(documentScanMapper.toDTO(doc)).thenReturn(dto);

        ResponseEntity<DocumentScanDTO> response=checkInController.getDocumentScanByCheckIn(id);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(dto,response.getBody());
    }
    @Test
    void testGetDocuementScanByCheckIn_ThrowsException(){
        Long id=1L;
        when(checkInService.getDocumentByCheckin(id)).thenThrow(new RuntimeException());
        ResponseEntity<DocumentScanDTO> response=checkInController.getDocumentScanByCheckIn(id);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
    @Test
    void testGetStatusCheckIn(){
        Long reservationId=1L;
        CheckInStatus status=CheckInStatus.VALIDE;
        when(checkInService.getStatusCheckIn(reservationId)).thenReturn(status);

        ResponseEntity<CheckInStatus> response=checkInController.getStatusCheckIn(reservationId);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(status,response.getBody());
    }
    @Test
    void testGetCheckInStatus(){
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        CheckIn checkIn = new CheckIn();
        checkIn.setStatus(CheckInStatus.VALIDE);

        reservation.setCheckIn(checkIn);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));


        ResponseEntity<String> response =checkInController.getCheckInStatus(reservationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("VALIDE", response.getBody());
    }
    @Test
    void testGetCheckInStatus_ReservationNotFoundException(){
        Long reservationId = 1L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = checkInController.getCheckInStatus(reservationId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("not_found", response.getBody());
    }
    @Test
    void testGetCheckInStatus_CheckInNull(){
        Long reservationId = 1L;
        Reservation reservation = new Reservation();
        reservation.setCheckIn(null);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        ResponseEntity<String> response = checkInController.getCheckInStatus(reservationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("no_checkin", response.getBody());
    }
    @Test
    void testValiderCheckInReception(){
        Long checkinId=1L;
        doNothing().when(checkInService).validerCheckinReception(checkinId);

        ResponseEntity<String> response=checkInController.validercheckinReception(checkinId);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Check-in validé avec succès",response.getBody());
    }
    @Test
    void testAjouterCheckIn_success() throws IOException {
        Long reservationId=1L;
        String nom="Sara";
        String prenom="Akharraz";
        DocumentScanType type=DocumentScanType.CIN;
        String cin="ABCDE123";
        String passport=null;

        byte[] imageBytes=new byte[]{1,2,3};
        MockMultipartFile image=new MockMultipartFile(
                "image","doc.png","image/png",imageBytes
        );

        DocumentScan doc=new DocumentScan();

        when(documentScanMapper.toEntity(any(DocumentScanDTO.class), isNull()))
                .thenReturn(doc);
        doNothing().when(checkInService).ajoutercheckinReception(reservationId, doc);
        ResponseEntity<String> response = checkInController.ajoutercheckin(
                reservationId, nom, prenom, type, cin, passport, image
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Check-in ajouté avec succès", response.getBody());

        verify(documentScanMapper).toEntity(any(DocumentScanDTO.class), isNull());
        verify(checkInService).ajoutercheckinReception(reservationId,doc);
    }
    @Test
    void testGetCheckInByReservation_success(){
        Long reservationId = 1L;
        CheckIn checkIn = new CheckIn();
        CheckInDTO dto = new CheckInDTO();

        when(checkInService.getCheckInByReservation(reservationId)).thenReturn(checkIn);
        when(checkInMapper.toDTO(checkIn)).thenReturn(dto);

        ResponseEntity<ApiResponse<CheckInDTO>> response = checkInController.getCheckInByReservation(reservationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Check-in trouvé.", response.getBody().getMessage());
        assertEquals(dto, response.getBody().getData());
    }
    @Test
    void testGetCheckInByReservation_ReservationNotFoundException(){
        Long reservationId = 1L;
        when(checkInService.getCheckInByReservation(reservationId)).thenReturn(null);
        when(reservationService.existsById(reservationId)).thenReturn(false);

        ResponseEntity<ApiResponse<CheckInDTO>> response = checkInController.getCheckInByReservation(reservationId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Réservation introuvable.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void testGetCheckInByReservation_CheckInNull(){
        Long reservationId = 1L;

        when(checkInService.getCheckInByReservation(reservationId)).thenReturn(null);
        when(reservationService.existsById(reservationId)).thenReturn(true);

        ResponseEntity<ApiResponse<CheckInDTO>> response = checkInController.getCheckInByReservation(reservationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Check-in non encore effectué.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void testGetCheckInByReservation_ThrowsException(){
        Long reservationId = 1L;

        when(checkInService.getCheckInByReservation(reservationId)).thenThrow(new RuntimeException());

        ResponseEntity<ApiResponse<CheckInDTO>> response = checkInController.getCheckInByReservation(reservationId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
    @Test
    void testValiderCheckIn_ReservationNotFound(){
        Long reservationId=1L;

        when(reservationService.getReservationById(reservationId)).thenReturn(null);
        ResponseEntity<String> response=checkInController.validerCheckIn(reservationId);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals("Réservation non trouvée.",response.getBody());
    }
    @Test
    void testValiderCheckIn_Echec(){
        Long reservationId=1L;
        Reservation res=new Reservation();
        when(reservationService.getReservationById(reservationId)).thenReturn(res);
        ResponseEntity<String> response=checkInController.validerCheckIn(reservationId);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals("Échec de validation du check-in.",response.getBody());
    }
    @Test
    void testValiderCheckIn_success(){
        Long reservationId=1L;
        Reservation res=new Reservation();

        when(reservationService.getReservationById(reservationId)).thenReturn(res);
        when(checkInService.validerCheckIn(res)).thenReturn(true);
        ResponseEntity<String> response=checkInController.validerCheckIn(reservationId);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Check-in validé avec succès.",response.getBody());
    }
    @Test
    void testValiderCheckIn_ThrowsException(){
        Long reservationId=1L;
        Reservation res=new Reservation();

        when(reservationService.getReservationById(reservationId)).thenReturn(res);
        when(checkInService.validerCheckIn(res)).thenThrow(new RuntimeException("Erreur interne"));
        ResponseEntity<String> response=checkInController.validerCheckIn(reservationId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
        assertEquals("Une erreur interne s'est produite.",response.getBody());
    }
    @Test
    void testValiderScan_InvalidMIME() throws Exception{

        when(file.getContentType()).thenReturn("application/pdf");
        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, "Sara", "Akharraz", "AB123456", "CIN");

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(false,response.getBody().get("success"));
        assertEquals("Format de fichier non supporté.",response.getBody().get("error"));
    }
    @Test
    void testValiderScan_InvalidTaille() throws Exception{

        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(6 * 1024 * 1024L);
        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, "Sara", "Akharraz", "AB123456", "CIN");
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(false,response.getBody().get("success"));
        assertEquals("Le fichier est trop volumineux.",response.getBody().get("error"));
    }
    @Test
    void testValiderScan_InvalidField() throws Exception{
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(5 * 1024 * 1024L);
        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, null, "Akharraz", "AB123456", "CIN");
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(false,response.getBody().get("success"));
        assertEquals("Les informations (nom, prénom, CIN) sont requises.",response.getBody().get("error"));
    }
    @Test
    void testValiderScan_InvalidTypeOfDocument() throws Exception{
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(5 * 1024 * 1024L);
        when(file.getBytes()).thenReturn(new byte[]{1});

        String invalidType = "WRONGTYPE";

        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, "Sara", "Akharraz", "AB123456", invalidType);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(false,response.getBody().get("success"));
        assertEquals("Type de document invalide.",response.getBody().get("error"));
    }
    @Test
    void testValiderScan_success()throws Exception{
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(5 * 1024 * 1024L);
        when(file.getBytes()).thenReturn(new byte[]{1});
        when(file.getOriginalFilename()).thenReturn("test.jpg");


        when(checkInService.validerScan(any(), any())).thenReturn(true);

        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, "Sara", "Akharraz", "AB123456", "CIN");
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(true,response.getBody().get("success"));
        assertEquals("Scan validé avec succès ",response.getBody().get("message"));
    }
    @Test
    void testValiderScan_Echec() throws Exception{
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(5 * 1024 * 1024L);
        when(file.getBytes()).thenReturn(new byte[]{1});
        when(file.getOriginalFilename()).thenReturn("test.jpg");


        when(checkInService.validerScan(any(), any())).thenReturn(false);

        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, "Sara", "Akharraz", "AB123456", "CIN");
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(false,response.getBody().get("success"));
        assertEquals("Échec de validation du document check-in.",response.getBody().get("error"));
    }
    @Test
    void testValiderScan_ThrowsIOException() throws Exception{
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(5 * 1024 * 1024L);
        when(file.getBytes()).thenThrow(new IOException("erreur lors de la lecture du fichier"));

        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, "Sara", "Akharraz", "AB123456", "CIN");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
        assertEquals(false,response.getBody().get("success"));
        assertEquals("Erreur lors de la lecture du fichier.",response.getBody().get("error"));
    }
    @Test
    void testValiderScan_ThrowsRuntimeException() throws Exception{
        Reservation res=new Reservation();

        when(reservationService.getReservationById(1L)).thenReturn(res);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(5 * 1024 * 1024L);
        when(file.getBytes()).thenReturn(new byte[]{1});

        when(checkInService.validerScan(any(), any())).thenThrow(new RuntimeException("erreur service"));

        ResponseEntity<Map<String, Object>> response=checkInController.validerScan(1L, file, "Sara", "Akharraz", "AB123456", "CIN");
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals(false,response.getBody().get("success"));
        assertEquals("erreur service", response.getBody().get("error"));
    }

}
