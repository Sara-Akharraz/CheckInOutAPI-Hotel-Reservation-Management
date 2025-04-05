package com.example.demo.ServiceImpTest;

import com.example.demo.Repository.ReservationRepository;
import com.example.demo.Service.Impl.ReservationServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationServiceImpl reservationService;

}
