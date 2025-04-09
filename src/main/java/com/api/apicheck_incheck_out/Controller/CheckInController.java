package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.DocumentScanMock.Service.DocumentScanService;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Service.CheckInService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/check_in")
public class CheckInController {
    private final CheckInService checkInService;
    private final DocumentScanService documentScanService;
    private final ReservationService reservationService;
    private final DocumentScanMapper documentScanMapper;

    public CheckInController(CheckInService checkInService, DocumentScanService documentScanService, ReservationService reservationService, DocumentScanMapper documentScanMapper) {
        this.checkInService = checkInService;
        this.documentScanService = documentScanService;
        this.reservationService = reservationService;
        this.documentScanMapper = documentScanMapper;
    }
    @PostMapping("/validerScan")
    public ResponseEntity<String> validerScan(@RequestParam Long reservationId, @RequestParam Long docId){
        try{
            DocumentScanDTO doc=documentScanService.getDocScanMock(docId);
            Reservation reservation=reservationService.getReservationById(reservationId);
            Boolean result=checkInService.validerScan(reservation,doc);

            return result
                         ?ResponseEntity.ok("Check-in document validé avec succès.")
                         :ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Echec de validation Check-in document");
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<DocumentScanDTO> getDocumentScanByCheckIn(@PathVariable Long id){
        try{
            DocumentScanDTO doc=documentScanMapper.toDTO(checkInService.getDocumentByChekin(id));
            return ResponseEntity.ok(doc);
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/validerCheckIn")
    public ResponseEntity<String> validerCheckIn(@RequestParam Long reservationId,
                                                 @RequestParam PaiementMethod method) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Réservation non trouvée.");
            }

            Boolean result = checkInService.validerCheckIn(reservation, method);

            return result
                    ? ResponseEntity.ok("Check-in validé avec succès.")
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Echec de validation du check-in.");

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne s'est produite.");
        }
    }
}
