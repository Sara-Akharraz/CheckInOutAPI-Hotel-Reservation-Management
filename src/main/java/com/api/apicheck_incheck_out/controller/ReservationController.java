package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.dto.ReservationDTO;
import com.api.apicheck_incheck_out.dto.ReservationRequestDTO;
import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;
import com.api.apicheck_incheck_out.mapper.UserMapper;
import com.api.apicheck_incheck_out.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    private final UserMapper userMapper;

    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper, UserMapper userMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
        this.userMapper = userMapper;
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

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id){
        Reservation reservation =reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
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

    @GetMapping("/reservations/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsParUser(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);

        List<ReservationDTO> dtoList = reservations.stream()
                .map(reservationMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);

    }
    @GetMapping("/search")
    public List<ReservationDTO> searchReservations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) ReservationStatus status) {

        List<Reservation> reservations = reservationService.searchReservations(search, dateDebut, dateFin, status);

        return reservations.stream()
                .map(reservationMapper::toDTO)
                .toList();
    }


    @GetMapping("/details/{reservationId}")
    public ResponseEntity<DetailReservationRequestDTO> getReservationDetail(@PathVariable Long reservationId) {
        DetailReservationRequestDTO detail = reservationService.getReservationDetail(reservationId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("userinfo/{idReservation}")
    public ResponseEntity<UserDto> getInfoUser(@PathVariable Long idReservation){
        User user=reservationService.findUserByReservation(idReservation);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = reservationService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

}
