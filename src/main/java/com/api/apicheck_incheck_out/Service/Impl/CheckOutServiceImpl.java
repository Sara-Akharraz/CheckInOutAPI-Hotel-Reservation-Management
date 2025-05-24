package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.CheckOutStatut;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Repository.CheckOutRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.Service.*;
import com.api.apicheck_incheck_out.Stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.Stripe.StripeResponse;
import com.api.apicheck_incheck_out.Stripe.Service.StripeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheckOutServiceImpl implements CheckOutService {

    @Autowired
    CheckOutRepository checkOutRepository;

    @Autowired
    ReservationService reservationService;

    @Autowired
    StripeService stripeService;

    @Autowired
    @Lazy
    FactureService factureService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    ReservationServicesService reservationServicesService;
    @Autowired
    ServicesService servicesService;
    @Autowired
    ReservationServiceRepository reservationServiceRepository;
    @Autowired
    ReservationRepository reservationRepository;

    public Check_Out addCheckOut(Check_Out checkout) {
        if (checkout == null) {
            throw new IllegalArgumentException("Check_Out object cannot be null");
        }
        checkout.setCheckOutStatut(CheckOutStatut.EN_ATTENTE);
        return checkOutRepository.save(checkout);
    }

    @Override
    public Check_Out getCheckOutById(Long id) {
        Optional<Check_Out> checkout = checkOutRepository.findById(id);
        if (checkout.isPresent()) {
            return checkout.get();
        } else {
            throw new RuntimeException("Check Out not found with id: " + id);
        }
    }

    @Override
    public List<Check_Out> getAllCheckOuts() {
        return checkOutRepository.findAll();
    }

    @Override
    public Check_Out setCheck_OutStatus(Long id, CheckOutStatut newStatus) {
        Check_Out checkout = checkOutRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Check Out not found with id: " + id));
        checkout.setCheckOutStatut(newStatus);
        return checkOutRepository.save(checkout);
    }

    @Override
    public double getAmount(Long id) {
        Check_Out checkout = checkOutRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Check Out not found with id: " + id));
        Long id_reservation = checkout.getReservation().getId();
        List<ReservationServices>  reservationServices = reservationServicesService.getAllServicesByReservation(id_reservation);

        return reservationServices.stream()
                .mapToDouble(rsrvservice -> rsrvservice.getService().getPrix())
                .sum();
    }

    @Override
    public StripeResponse payer(Long id) {
        Check_Out checkOut = getCheckOutById(id);
        CheckOutRequest checkoutRequest = CheckOutRequest.builder()
                .idCheckQOut(id)
                .checkOutName("Services consommés pendant le séjour")
                .amount(Math.round(this.getAmount(id) * 100))
                .build();
        StripeResponse stripeResponse = stripeService.checkoutServices(checkoutRequest);
        if (stripeResponse != null && "SUCCESS".equals(stripeResponse.getStatus())) {
            return stripeResponse;
        }else
            throw new RuntimeException("Stripe payment failed");
    }
    @Override
    public void handlePaymentSuccess(Long id){
        Check_Out checkOut = getCheckOutById(id);
        checkOut.setDateCheckOut(LocalDate.now());
        checkOut.setCheckOutStatut(CheckOutStatut.CONFIRMEE);
        checkOut.getReservation().setStatus(ReservationStatus.Terminee);
        checkOutRepository.save(checkOut);
        Long idrsrv = checkOut.getReservation().getId();
        List<ReservationServices> rsrvServices = reservationServicesService.getServicesByPhase(idrsrv, PhaseAjoutService.sejour);
        List<Services> services = rsrvServices.stream()
                .map(rs -> rs.getService())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Reservation r = checkOut.getReservation();
        notificationService.notifier(r.getUser().getId(),"Votre paiement pour le checkout de réservation numéro "+ r.getId()+ " est confirmé !");
        double total = this.getAmount(checkOut.getId());
        factureService.validerPaiementCheckOut(r,total);
        FacturePDF.gerercheckOutFacturePDF(checkOut.getReservation(),services,total);
        rsrvServices.stream().forEach(service -> {
            service.setPaiementStatus(PaiementStatus.paye);
        });
        reservationServiceRepository.saveAll(rsrvServices);
    }
    @Override
    public Check_Out getCheckOutByReservation(Long Id_reservation) {

        Optional<Reservation> reservation = reservationRepository.findById(Id_reservation);
        if (reservation.isEmpty()) {
            throw new EntityNotFoundException("Réservation non trouvée avec l'id : " + Id_reservation);
        }

        return checkOutRepository.findByReservation(reservation.get()).orElse(null);

    }
}