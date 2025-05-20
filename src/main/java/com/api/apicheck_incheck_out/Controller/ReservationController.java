package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.DTO.ReservationRequestDTO;
import com.api.apicheck_incheck_out.DTO.UserDto;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.Mapper.UserMapper;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.api.apicheck_incheck_out.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    private final ReservationRepository reservationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper, ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;


        this.reservationRepository = reservationRepository;
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
//    @GetMapping
//    public ResponseEntity<List<ReservationDTO>> getAllReservation(){
//        List<ReservationDTO> reservationDTOList=reservationService.getAllReservations().stream()
//                .map(reservationMapper::toDTO)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(reservationDTOList);
//    }
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
                .collect(Collectors.toList());

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
                .collect(Collectors.toList());
    }

//    @GetMapping("/all")
//    public List<ReservationDTO> getAll() {
//        List<Reservation> reservations = reservationRepository.findAll();
//        return reservations.stream()
//                .map(reservationMapper::toDTO)
//                .collect(Collectors.toList());
//    }


    @GetMapping("/details/{reservationId}")
    public ResponseEntity<DetailReservationRequestDTO> getReservationDetail(@PathVariable Long reservationId) {
        DetailReservationRequestDTO detail = reservationService.getReservationDetail(reservationId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("userinfo/{id_reservation}")
    public ResponseEntity<UserDto> getInfoUser(@PathVariable Long id_reservation){
        User user=reservationService.findUserByReservation(id_reservation);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = reservationService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

}
