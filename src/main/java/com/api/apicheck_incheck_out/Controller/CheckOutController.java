package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.CheckInDTO;
import com.api.apicheck_incheck_out.DTO.CheckOutDTO;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.CheckOutStatut;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Mapper.CheckInMapper;
import com.api.apicheck_incheck_out.Mapper.CheckOutMapper;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.CheckOutService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.api.apicheck_incheck_out.Service.ReservationServicesService;
import com.api.apicheck_incheck_out.Stripe.StripeResponse;
//import com.api.apicheck_incheck_out.Service.paypalPayment.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/checkout")
public class CheckOutController {
    @Autowired
    CheckOutService checkOutService;

    @Autowired
    CheckOutMapper checkOutMapper;
    @Autowired
    ReservationServicesService reservationServicesService;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    ReservationService reservationService;
    @GetMapping
    public ResponseEntity<?> getCheckOuts() {
        try {
            List<Check_Out> checkouts = checkOutService.getAllCheckOuts();
            return new ResponseEntity<>(checkOutMapper.toDTOList(checkouts), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id_checkOut}")
    public ResponseEntity<?> getCheckOut(@PathVariable("id_checkOut") Long id_checkOut) {
        try {
            return ResponseEntity.ok(checkOutMapper.toDTO(checkOutService.getCheckOutById(id_checkOut)));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/reservation/{Id_reservation}")
    public ResponseEntity<?> getCheckOutByReservation(@PathVariable Long Id_reservation) {
        try {
            System.out.println("===> Recherche Check-in pour réservation ID: " + Id_reservation);
            Check_Out checkOut = checkOutService.getCheckOutByReservation(Id_reservation);
            if (checkOut != null) {
                CheckOutDTO checkOutDTO = checkOutMapper.toDTO(checkOut);
                return ResponseEntity.ok(checkOutDTO);
            } else {
                System.out.println("===> checkIn is null, checking reservation existence...");
                boolean exists = reservationService.existsById(Id_reservation);
                System.out.println("===> Reservation exists? " + exists);
                if (reservationService.existsById(Id_reservation)) {
                    return ResponseEntity.ok("Check-in non encore effectué.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable.");
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }


    @PostMapping
    public ResponseEntity<?> addCheckOut(@RequestBody CheckOutDTO check_out) {
        try {
            Check_Out checkout = checkOutMapper.toEntity(check_out);
            return ResponseEntity.ok(checkOutMapper.toDTO(checkOutService.addCheckOut(checkout)));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<?> setCheck_OutStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        try {
            return ResponseEntity.ok(checkOutMapper.toDTO(checkOutService.setCheck_OutStatus(id, CheckOutStatut.valueOf(status))));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/amount/{id}")
    public double getAmount(@PathVariable("id") Long id) {
        return checkOutService.getAmount(id);
    }

    @GetMapping("/payer/{id_checkout}")
    public StripeResponse payer(@PathVariable("id_checkout") Long id) {
        try {
            return checkOutService.payer(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/validate-payment/{id_checkout}")
    public void handlePaymentSuccess(@PathVariable("id_checkout") Long id) {
        try {
            checkOutService.handlePaymentSuccess(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }


    }

    @GetMapping("/facture/{reservationId}")
    public ResponseEntity<byte[]> afficherFactureDansNavigateur(@PathVariable Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + reservationId));
        List<ReservationServices> reservationsServicesSejour = reservationServicesService.getServicesByPhase(
                reservation.getId(),
                PhaseAjoutService.sejour
        );
        List<Services> services = reservationsServicesSejour
                .stream()
                .map(reservationService -> reservationService.getService())
                .collect(Collectors.toList());
        byte[] pdfBytes = FacturePDF.gerercheckOutFacturePDF(reservation, services, checkOutService.getAmount(reservation.getCheckOut().getId()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=facture.pdf")
                .body(pdfBytes);

    }


//    @Autowired
//    private PayPalService payPalService;
//
//    @GetMapping("/paypal-payment/{idReservation}")
//    public Map<String, Object> createPayment(@PathVariable("idReservation") Long id) {
//
//        try {
//            String cancelUrl = "http://localhost:3000/Services-sejour";
//            String successUrl = "http://localhost:3000/checkout-payment-success";
//            Reservation reservation = reservationService.getReservationById(id);
//            Check_Out checkout = checkOutService.getCheckOutById(reservation.getCheckOut().getId());
//            Double amount = checkOutService.getAmount(checkout.getId());
//            String description = "Les services consommées pendant le séjour";
//            Payment payment = payPalService.createPayment(
//                    amount/10.0,
//                    "USD",
//                    "paypal",
//                    "sale",
//                    description,
//                    cancelUrl,
//                    successUrl);
//
//            Map<String, Object> response = new HashMap<>();
//            for(Links link:payment.getLinks()) {
//                if(link.getRel().equals("approval_url")) {
//                    response.put("redirectUrl", link.getHref());
//                }
//            }
//            response.put("paymentId", payment.getId());
//            return response;
//
//        } catch (PayPalRESTException e) {
//            throw new ResponseStatusException(
//                    HttpStatus.INTERNAL_SERVER_ERROR, "Error creating PayPal payment", e);
//        }
//    }
}
