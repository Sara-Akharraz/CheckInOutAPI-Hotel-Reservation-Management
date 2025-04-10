package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.Entity.FacturePDF;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/facture")
public class FactureController {
    @Autowired
    private FactureService factureService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;

    @PostMapping("payer_checkin")
    public ResponseEntity<String> payerFactureCheckIn(@RequestParam Long reservationId, @RequestParam PaiementMethod method){
        try{
            Reservation reservation=reservationService.getReservationById(reservationId);
            if(reservation==null){
                return ResponseEntity.badRequest().body("Réservation introuvable avec l'id "+reservationId);
            }
            boolean result=factureService.payerFactureCheckIn(reservation,method);
            return result
                    ?ResponseEntity.ok("Paiement effectué avec succès")
                    :ResponseEntity.badRequest().body("Echec de paiement .");

        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Erreur lors du processus paiement :" +e.getMessage());
        }
    }
    @GetMapping("/checkinfacture/{reservationId}")
    public ResponseEntity<byte[]> afficherFactureDansNavigateur(@PathVariable Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + reservationId));

        byte[] pdfBytes = FacturePDF.gerercheckinFacturePDF(reservation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename("facture_checkin_" + reservationId + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
