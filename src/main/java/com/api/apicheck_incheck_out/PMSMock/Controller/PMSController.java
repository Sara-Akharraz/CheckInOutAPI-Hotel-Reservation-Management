package com.api.apicheck_incheck_out.PMSMock.Controller;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pms")
public class PMSController {
    private final PMSService pmsService;

    @Autowired
    public PMSController(PMSService pmsService) {
        this.pmsService = pmsService;
    }
    @PostMapping("/demandeReservation")
    public ResponseEntity<ReservationDTO> demanderReservation(@RequestBody ReservationDTO reservationDTO){
        if(reservationDTO.getStatus()==null){
            reservationDTO.setStatus(ReservationStatus.En_Attente);
        }
        ReservationDTO demandeCree =pmsService.demanderReservation(reservationDTO);
        return ResponseEntity.ok(demandeCree);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getDemandeReservationById(@PathVariable Long id){
        ReservationDTO demandeTrouvee=pmsService.getDemandeReservationById(id);
        if(demandeTrouvee !=null){
            return ResponseEntity.ok(demandeTrouvee);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllDemandeReservation(){
        List<ReservationDTO> reservationDTOList=pmsService.getAllDemandeReservation();
        return ResponseEntity.ok(reservationDTOList);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateDemandeReservation(@PathVariable Long id,@RequestBody ReservationDTO reservationDTO){
       ReservationDTO reservationDTOUpdated=pmsService.updateDemandeReservation(id,reservationDTO);
        return ResponseEntity.ok(reservationDTOUpdated);
    }
}
