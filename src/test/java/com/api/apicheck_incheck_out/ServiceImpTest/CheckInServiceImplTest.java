package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Entity.DocumentScan;
import com.api.apicheck_incheck_out.DocumentScanMock.Repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enums.CheckInStatus;
import com.api.apicheck_incheck_out.Enums.DocumentScanType;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Repository.CheckInRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.api.apicheck_incheck_out.Service.Impl.CheckInServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckInServiceImplTest {
    @Mock
    private CheckInRepository checkInRepository;
    @Mock
    private DocumentScanRepository documentScanRepository;
    @Mock
    private FactureService factureService;
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private CheckInServiceImpl checkInService;

    private Reservation reservation;
    private User user;
    @BeforeEach
    void setUp(){
        user=new User();
        user.setNom("test");
        user.setPrenom("test");
        user.setCin("AA123456");
        user.setNumeroPassport("P123456");

        reservation =new Reservation();
        reservation.setUser(user);
        reservation.setFactureList(Collections.emptyList());

    }
    @Test
    public void TestvaliderScan(){
        DocumentScanDTO doc=new DocumentScanDTO();
        doc.setNom("test");
        doc.setPrenom("test");
        doc.setCin("AA123456");
        doc.setType(DocumentScanType.CIN);

        boolean result= checkInService.validerScan(reservation,doc);

        assertTrue(result);
        verify(documentScanRepository).save(any(DocumentScan.class));
        verify(checkInRepository).save(any(Check_In.class));
    }
    @Test
    public void TestgetDocScanByCheckin(){
        DocumentScan doc=new DocumentScan();
        Check_In checkIn=new Check_In();
        checkIn.setDocumentScan(doc);
        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));

        DocumentScan result=checkInService.getDocumentByCheckin(1L);
        assertEquals(doc,result);
    }
    @Test
    public void TestValiderCheckIn(){
        DocumentScan doc=new DocumentScan();
        Check_In checkIn=new Check_In();
        checkIn.setStatus(CheckInStatus.En_Attente);
        checkIn.setDocumentScan(doc);
        checkIn.setReservation(reservation);

        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));
        when(factureService.payerFactureCheckIn(reservation, PaiementMethod.STRIPE)).thenReturn(true);

        boolean result=checkInService.validerCheckIn(reservation,PaiementMethod.STRIPE);

        assertTrue(result);
        assertEquals(CheckInStatus.Validé,checkIn.getStatus());
        verify(reservationRepository).save(reservation);
    }
}
