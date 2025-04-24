package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.FacturePDF;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enum.CheckOutStatut;
import com.api.apicheck_incheck_out.Repository.CheckOutRepository;
import com.api.apicheck_incheck_out.Service.CheckOutService;
import com.api.apicheck_incheck_out.Service.ExtraServicesService;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.api.apicheck_incheck_out.Service.StripePayment.CheckOutRequest;
import com.api.apicheck_incheck_out.Service.StripePayment.StripeResponse;
import com.api.apicheck_incheck_out.Service.StripePayment.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CheckOutServiceImpl implements CheckOutService {

    @Autowired
    CheckOutRepository checkOutRepository;

    @Autowired
    ReservationService reservationService;

    @Autowired
    StripeService stripeService;

    @Autowired
    FactureService factureService;

    @Autowired
    ExtraServicesService extraServicesService;


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
    public boolean isValidCheckOut(Long id) {
        Check_Out checkout = checkOutRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Check Out not found with id: " + id));
        Long id_reservation = checkout.getReservation().getId();
        return extraServicesService.calculateTotalExtraPrice(id_reservation)==0;
    }

    @Override
    public Long getAmount(Long id) {
        double amount = extraServicesService.calculateTotalExtraPrice(id);
        return Long.valueOf((long) amount);
    }

    @Override
    public StripeResponse payer(Long id) {
        Check_Out checkOut = getCheckOutById(id);
        CheckOutRequest checkoutRequest = CheckOutRequest.builder()
                .checkOutName("Extra Services Payment")
                .amount(this.getAmount(checkOut.getReservation().getId()))
                .build();
        StripeResponse stripeResponse = stripeService.checkoutServices(checkoutRequest);
        if (stripeResponse != null && "SUCCESS".equals(stripeResponse.getStatus())) {
            factureService.validerPaiementCheckOut(checkOut.getReservation());
            FacturePDF.gerercheckOutFacturePDF(checkOut.getReservation(),
                    extraServicesService.getExtrasOfReservation(checkOut.getReservation().getId()));
            System.out.println(extraServicesService.getExtrasOfReservation(checkOut.getReservation().getId()));
            return stripeResponse;
        }else
            throw new RuntimeException("Stripe payment failed");
}
}