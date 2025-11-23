package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.PaiementRequestDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.FactureType;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.repository.FactureRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.CheckOutService;
import com.api.apicheck_incheck_out.service.FactureService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/facture")
public class FactureController {

    private final FactureService factureService;


    private  final ReservationRepository reservationRepository;

    private final StripeServiceImpl stripeService;

    private final FactureRepository factureRepository;

    private ReservationServicesService reservationServicesService;

    private final CheckOutService checkOutService;

    private static final String ERROR = "error";

    public FactureController(FactureService factureService,ReservationRepository reservationRepository, StripeServiceImpl stripeService, FactureRepository factureRepository, CheckOutService checkOutService) {
        this.factureService = factureService;
        this.reservationRepository = reservationRepository;
        this.stripeService = stripeService;
        this.factureRepository = factureRepository;
        this.checkOutService = checkOutService;
    }

    @GetMapping("Montant_checkin")
    public ResponseEntity<Double> getMontantCheckIn(@RequestParam Long idReservation){
        Reservation reservation=reservationRepository.findById(idReservation)
                .orElseThrow(()->new RuntimeException("Reservation non trouvée par l'id :"+idReservation));
        double montantCheckIn = factureService.calculerMontantCheckIn(reservation);

        return ResponseEntity.ok(montantCheckIn);
    }
    @GetMapping("Montant_checkOut")
    public ResponseEntity<Double> getMontantCheckOut(@RequestParam Long idReservation){
        Reservation reservation=reservationRepository.findById(idReservation)
                .orElseThrow(()->new RuntimeException("Reservation non trouvée par l'id :"+idReservation));
        double montantCheckOut = checkOutService.getAmount(reservation.getCheckOut().getId());

        return ResponseEntity.ok(montantCheckOut);
    }
@GetMapping("/checkinfacture/{factureId}")
public ResponseEntity<byte[]> afficherFactureDansNavigateur(@PathVariable Long factureId) {
    Facture facture = factureRepository.findById(factureId)
            .orElseThrow(() -> new RuntimeException("Facture introuvable avec ID : " + factureId));

    Reservation reservation = facture.getReservation();
    if (reservation == null) {
        throw new NoSuchElementException("Aucune réservation trouvée pour la facture avec ID : " + factureId);
    }
    List<ReservationServices> reservationsServicesSejour = reservationServicesService.getServicesByPhase(
            reservation.getId(),
            PhaseAjoutService.sejour
    );
    List<Services> services = reservationsServicesSejour
            .stream()
            .map(ReservationServices::getService)
            .toList();

    byte[] pdfBytes;
    if(facture.getType().equals(FactureType.Check_In)){
         pdfBytes = FacturePDF.gerercheckinFacturePDF(reservation);

    }else{
        pdfBytes = FacturePDF.gerercheckOutFacturePDF(reservation, services, checkOutService.getAmount(reservation.getCheckOut().getId()));
    }


    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.builder("inline")
            .filename("facture "+ facture.getType().toString() + " .pdf")
            .build());

    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
}
    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {

            Object amountObj = request.get("amount");
            if (amountObj == null) {
                return ResponseEntity.badRequest().body(Map.of(ERROR, "Le montant est requis."));
            }

            double amount;
            try {
                amount = Double.parseDouble(amountObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of(ERROR, "Montant invalide."));
            }

            if (amount <= 0) {
                return ResponseEntity.badRequest().body(Map.of(ERROR, "Le montant doit être supérieur à zéro."));
            }

            // Création du PaymentIntent
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount);
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR, "Erreur Stripe : " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR, "Erreur interne : " + e.getMessage()));
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, e.getMessage()));
        }
    }

    @GetMapping("/factures/{reservationId}/{userId}")
    public ResponseEntity<List<Facture>> getAllFacture(
            @PathVariable Long reservationId,
            @PathVariable Long userId) {


        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + reservationId));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Accès interdit pour l'utilisateur ID : " + userId);
        }
        List<Facture> factures = factureRepository.findAllByReservation_Id(reservationId);


        return new ResponseEntity<>(factures, HttpStatus.OK);
    }
    @GetMapping("/factures/{reservationId}")
    public ResponseEntity<List<Facture>> getAllFactureCheckin(@PathVariable Long reservationId) {

        reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + reservationId));



        List<Facture> factures = factureRepository.findAllByReservation_Id(reservationId);

        return new ResponseEntity<>(factures, HttpStatus.OK);
    }


}
