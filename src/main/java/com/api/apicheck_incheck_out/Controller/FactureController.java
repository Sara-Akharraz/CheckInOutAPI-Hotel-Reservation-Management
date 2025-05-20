package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.PaiementRequestDTO;
import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.FacturePDF;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.FactureType;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Repository.FactureRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.api.apicheck_incheck_out.Stripe.Service.Impl.StripeServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facture")
public class FactureController {
    @Autowired
    private FactureService factureService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private StripeServiceImpl stripeService;
    @Autowired
    private FactureRepository factureRepository;

    @GetMapping("Montant_checkin")
    public ResponseEntity<Double> getMontantCheck_In(@RequestParam Long id_reservation){
        Reservation reservation=reservationRepository.findById(id_reservation)
                .orElseThrow(()->new RuntimeException("Reservation non trouvée par l'id :"+id_reservation));
        double montantCheckIn = factureService.calculerMontantCheckIn(reservation);

        return ResponseEntity.ok(montantCheckIn);
    }

//    @PostMapping("payer_checkin")
//    public ResponseEntity<String> payerFactureCheckIn(@RequestBody PaiementRequestDTO paiementRequestDTO){
////        try{
////            Reservation reservation=reservationService.getReservationById(reservationId);
////            if(reservation==null){
////                return ResponseEntity.badRequest().body("Réservation introuvable avec l'id "+reservationId);
////            }
////            boolean result=factureService.payerFactureCheckIn(reservation,method);
////            return result
////                    ?ResponseEntity.ok("Paiement effectué avec succès")
////                    :ResponseEntity.badRequest().body("Echec de paiement .");
////
////        }catch (Exception e){
////            return ResponseEntity.internalServerError().body("Erreur lors du processus paiement :" +e.getMessage());
////        }
//        try {
//            Reservation reservation = reservationRepository.findById(paiementRequestDTO.getReservationId())
//                    .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'ID " + paiementRequestDTO.getReservationId()));
//
//            Boolean result = factureService.payerFactureCheckIn(paiementRequestDTO);
//
//            if (result) {
//                return ResponseEntity.ok("Paiement effectué avec succès");
//            } else {
//                return ResponseEntity.badRequest().body("Echec du paiement.");
//            }
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Erreur lors du processus paiement : " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
//        }
//    }
@GetMapping("/checkinfacture/{factureId}")
public ResponseEntity<byte[]> afficherFactureDansNavigateur(@PathVariable Long factureId) {
    Facture facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new RuntimeException("Facture introuvable avec ID : " + factureId));

    Reservation reservation = facture.getReservation();
    if (reservation == null) {
        throw new RuntimeException("Aucune réservation trouvée pour la facture avec ID : " + factureId);
    }

    byte[] pdfBytes = FacturePDF.gerercheckinFacturePDF(reservation);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.builder("inline")
            .filename("facture_checkin_" + factureId + ".pdf")
            .build());

    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
}
    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {

            Object amountObj = request.get("amount");
            if (amountObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le montant est requis."));
            }

            double amount;
            try {
                amount = Double.parseDouble(amountObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Montant invalide."));
            }

            if (amount <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le montant doit être supérieur à zéro."));
            }

            // Création du PaymentIntent
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount);
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur Stripe : " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne : " + e.getMessage()));
        }
    }
    @PostMapping("/payer_checkin")
    public ResponseEntity<Map<String, Object>> payerFactureCheckIn(@RequestBody PaiementRequestDTO paiementRequest) {
        try {
            Boolean paiementStatus = factureService.payerFactureCheckIn(paiementRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("success", paiementStatus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/facturescheckin/{reservationId}/{userId}")
    public ResponseEntity<List<Facture>> getAllFacture(
            @PathVariable Long reservationId,
            @PathVariable Long userId) {


        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + reservationId));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Accès interdit pour l'utilisateur ID : " + userId);
        }


        List<Facture> factures = factureRepository.findAllByReservation_IdAndType(reservationId, FactureType.Check_In);


        return new ResponseEntity<>(factures, HttpStatus.OK);
    }
    @GetMapping("/facturescheckin/{reservationId}")
    public ResponseEntity<List<Facture>> getAllFactureCheckin(@PathVariable Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + reservationId));

        // Si tu veux toujours ajouter une couche de sécurité, utilise Spring Security ou une session utilisateur ici

        List<Facture> factures = factureRepository.findAllByReservation_IdAndType(reservationId, FactureType.Check_In);

        return new ResponseEntity<>(factures, HttpStatus.OK);
    }

}
