package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.DTO.ReservationRequestDTO;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final PMSService pmsService;

    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper, PMSService pmsService) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
        this.pmsService = pmsService;
    }
    @PostMapping
    public ResponseEntity<ReservationDTO> addReservation(@RequestBody ReservationRequestDTO reservationRequestDTO){
        ReservationDTO reservationDTO = reservationRequestDTO.getReservationDTO();
        List<Long> chambresId = reservationRequestDTO.getChambresId();

        System.out.println("Données reçues : " + reservationDTO);
        System.out.println("Données reçues chambres : " + chambresId);

        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        Reservation newReservation = reservationService.addReservation(reservation, chambresId);

        return new ResponseEntity<>(reservationMapper.toDTO(newReservation), HttpStatus.CREATED);
    }
    //    @PostMapping("/fromPMS/{id}")
//    public ResponseEntity<ReservationDTO> addReservationFromPMS(@PathVariable Long id){
//        Reservation pmsReservation=reservationService.addReservationPMS(id);
//        return new ResponseEntity<>(reservationMapper.toDTO(pmsReservation),HttpStatus.CREATED) ;
//
//    }
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id){
        Reservation reservation =reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
    }
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservation(){
        List<ReservationDTO> reservationDTOList=reservationService.getAllReservations().stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservationDTOList);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationDTO> updateReservationStatus(@PathVariable Long id, @RequestBody ReservationStatus status){
        Reservation reservation=reservationService.updateReservationStatus(id,status);
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id){
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }


}