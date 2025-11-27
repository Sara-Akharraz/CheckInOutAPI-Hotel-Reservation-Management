package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.ApiResponse;
import com.api.apicheck_incheck_out.dto.CheckOutDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.mapper.CheckOutMapper;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.CheckOutService;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.pdf.CheckoutFacturePDF;
import com.api.apicheck_incheck_out.stripe.StripeResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/checkout")
public class CheckOutController {

    private final CheckOutService checkOutService;

    private final CheckOutMapper checkOutMapper;

    private final ReservationServicesService reservationServicesService;

    private final ReservationRepository reservationRepository;

    private final ReservationService reservationService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<CheckOutDTO>>> getCheckOuts() {
        try {
            List<CheckOut> checkouts = checkOutService.getAllCheckOuts();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des check-outs récupérée.", checkOutMapper.toDTOList(checkouts)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des check-outs: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{idCheckOut}")
    public ResponseEntity<ApiResponse<CheckOutDTO>> getCheckOut(@PathVariable("idCheckOut") Long idCheckOut) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true,"CheckOut trouvé",checkOutMapper.toDTO(checkOutService.getCheckOutById(idCheckOut))));
        } catch (Exception e) {
            return  ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    @GetMapping("/reservation/{idReservation}")
    public ResponseEntity<ApiResponse<CheckOutDTO>> getCheckOutByReservation(@PathVariable Long idReservation) {
        try {
            log.debug("===> Recherche Check-out pour réservation ID: " + idReservation);
            CheckOut checkOut = checkOutService.getCheckOutByReservation(idReservation);
            if (checkOut != null) {
                CheckOutDTO checkOutDTO = checkOutMapper.toDTO(checkOut);
                return ResponseEntity.ok(new ApiResponse<>(true,"Check-out trouvé.",checkOutDTO));
            } else {
                log.debug("===> checkOut is null, checking reservation existence...");
                boolean exists = reservationService.existsById(idReservation);
                log.debug("===> Reservation exists? " + exists);
                if (reservationService.existsById(idReservation)) {
                    return ResponseEntity.ok(new ApiResponse<>(false,"Check-out non encore effectué.",null));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"Réservation introuvable.", null));
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }


    @PostMapping
    public ResponseEntity<ApiResponse<CheckOutDTO>> addCheckOut(@RequestBody CheckOutDTO checkOut) {
        try {
            CheckOut checkout = checkOutMapper.toEntity(checkOut);
            return ResponseEntity.ok(new ApiResponse<>(true,"CheckOut a été bien ajouté",checkOutMapper.toDTO(checkOutService.addCheckOut(checkout))));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<ApiResponse<CheckOutDTO>> setCheckOutStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        try {
            CheckOut checkOut = checkOutService.setCheckOutStatus(id, CheckOutStatut.valueOf(status));
            CheckOutDTO checkOutDTO = checkOutMapper.toDTO(checkOut);
            return ResponseEntity.ok(new ApiResponse<>(true, "Statut du check-out mis à jour.", checkOutDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
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
        log.debug("Validation paiement check-out pour id : " + id);
        try {
            checkOutService.handlePaymentSuccess(id);
            log.info("Validation réussie pour check-out id: " + id);
        } catch (Exception e) {
            log.error("Erreur validation paiement: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    @GetMapping("/today-checkouts")
    public ResponseEntity<List<CheckOutDTO>> checkoutsForToday(    @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CheckOut> checkouts = checkOutService.checkoutsForToday(date);
        List<CheckOutDTO> dtos = checkouts.stream()
                .map(checkOutMapper::toDTO) // Using class mapper
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/facture/{reservationId}")
    public ResponseEntity<byte[]> afficherFactureDansNavigateur(@PathVariable Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + reservationId));
        List<ReservationServices> reservationsServicesSejour = reservationServicesService.getServicesByPhase(
                reservation.getId(),
                PhaseAjoutService.SEJOUR
        );
        List<Services> services = reservationsServicesSejour
                .stream()
                .map(ReservationServices::getService)
                .toList();
        byte[] pdfBytes = CheckoutFacturePDF.gerercheckOutFacturePDF(reservation, services, checkOutService.getAmount(reservation.getCheckOut().getId()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=facture.pdf")
                .body(pdfBytes);

    }

}
