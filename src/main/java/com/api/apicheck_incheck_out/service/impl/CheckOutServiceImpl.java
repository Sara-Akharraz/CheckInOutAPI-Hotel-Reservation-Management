package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.*;
import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.api.apicheck_incheck_out.stripe.service.StripeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service

@RequiredArgsConstructor
public class CheckOutServiceImpl implements CheckOutService {

    @Lazy
    private final FactureService factureService;

    private final NotificationService notificationService;

    private final ReservationServicesService reservationServicesService;

    private final ReservationServiceRepository reservationServiceRepository;

    private final ReservationRepository reservationRepository;

    private final CheckOutRepository checkOutRepository;

    private final StripeService stripeService;

    private final UserService userService;


    public CheckOut addCheckOut(CheckOut checkout) {
        if (checkout == null) {
            throw new IllegalArgumentException("Check_Out object cannot be null");
        }
        checkout.setCheckOutStatut(CheckOutStatut.EN_ATTENTE);
        return checkOutRepository.save(checkout);
    }

    @Override
    public CheckOut getCheckOutById(Long id) {
        Optional<CheckOut> checkout = checkOutRepository.findById(id);
        if (checkout.isPresent()) {
            return checkout.get();
        } else {
            throw new CheckOutNotFoundException("Check Out not found with id: " + id);
        }
    }

    @Override
    public List<CheckOut> getAllCheckOuts() {
        return checkOutRepository.findAll();
    }

    @Override
    public CheckOut setCheckOutStatus(Long id, CheckOutStatut newStatus) {
        CheckOut checkout = checkOutRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Check Out not found with id: " + id));
        checkout.setCheckOutStatut(newStatus);
        return checkOutRepository.save(checkout);
    }

    @Override
    public double getAmount(Long id) {
        CheckOut checkout = checkOutRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Check Out not found with id: " + id));
        Long idReservation = checkout.getReservation().getId();
        List<ReservationServices>  reservationServices = reservationServicesService.getAllServicesByReservation(idReservation);

        return reservationServices.stream()
                .mapToDouble(rsrvservice -> rsrvservice.getService().getPrix())
                .sum();
    }

    @Override
    public StripeResponse payer(Long id) {

        CheckOutRequest checkoutRequest = CheckOutRequest.builder()
                .idCheckQOut(id)
                .checkOutName("Services consommés pendant le séjour")
                .amount(Math.round(this.getAmount(id) * 100))
                .build();
        StripeResponse stripeResponse = stripeService.checkoutServices(checkoutRequest);
        if (stripeResponse != null && "SUCCESS".equals(stripeResponse.getStatus())) {
            return stripeResponse;
        }else
            throw new PaymentValidationException("Stripe payment failed");
    }
    @Override
    public void handlePaymentSuccess(Long id){
        CheckOut checkOut = getCheckOutById(id);
        checkOut.setDateCheckOut(LocalDate.now());
        checkOut.setCheckOutStatut(CheckOutStatut.CONFIRMEE);
        checkOut.getReservation().setStatus(ReservationStatus.TERMINEE);
        checkOutRepository.save(checkOut);
        Long idrsrv = checkOut.getReservation().getId();
        List<ReservationServices> rsrvServices = reservationServicesService.getServicesByPhase(idrsrv, PhaseAjoutService.SEJOUR);
        List<Services> services = rsrvServices.stream()
                .map(rs -> rs.getService())
                .filter(Objects::nonNull)
                .toList();
        Reservation r = checkOut.getReservation();

        double total = this.getAmount(checkOut.getId());
        factureService.validerPaiementCheckOut(r,total);
        FacturePDF.gerercheckOutFacturePDF(checkOut.getReservation(),services,total);
        rsrvServices.stream().forEach(service ->
            service.setPaiementStatus(PaiementStatus.PAYE)
        );
        notificationService.notifier(r.getUser().getId(),"Votre paiement pour le checkout de réservation numéro "+ r.getId()+ " est confirmé !");
        List<User> admins = userService.getAdmins();
        admins.stream().forEach(admin ->
            notificationService.notifier(admin.getId(),"Check-Out validé pour la réservation numéro ; "+ r.getId())

        );
        List<UserDto> receps = userService.getReceptionists();
        receps.stream().forEach(recep ->
            notificationService.notifier(recep.getId(),"Check-Out validé pour la réservation numéro ; "+ r.getId())
        );

        reservationServiceRepository.saveAll(rsrvServices);

    }
    @Override
    public List<CheckOut> checkoutsForToday(LocalDate today) {
        List<Reservation> reservations = reservationRepository.findByDateFin(today);
        return reservations.stream()
                .map(Reservation::getCheckOut)
                .filter(Objects::nonNull)
                .toList();
    }
    @Override
    public CheckOut getCheckOutByReservation(Long idReservation) {

        Optional<Reservation> reservation = reservationRepository.findById(idReservation);
        if (reservation.isEmpty()) {
            throw new EntityNotFoundException("Réservation non trouvée avec l'id : " + idReservation);
        }

        return checkOutRepository.findByReservation(reservation.get()).orElse(null);

    }
}