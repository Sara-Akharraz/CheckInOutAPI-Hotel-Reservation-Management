package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.dto.PaiementRequestDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;
import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import com.api.apicheck_incheck_out.repository.FactureRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.FactureService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FactureServiceImpl implements FactureService {


    private final FactureRepository factureRepository;


    private final ReservationRepository reservationRepository;

    private final ReservationServiceRepository reservationServiceRepository;

    private static final double TVA = 0.2;
    private static final double TAX = 10;

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String paypalMode;

    public FactureServiceImpl(FactureRepository factureRepository, ReservationRepository reservationRepository, ReservationServiceRepository reservationServiceRepository) {
        this.factureRepository = factureRepository;
        this.reservationRepository = reservationRepository;
        this.reservationServiceRepository = reservationServiceRepository;

    }

    @Override
    public void payerFactureCheckIn(Reservation reservation) {

        double montantCheckIn = calculerMontantCheckIn(reservation);

        Facture facture = new Facture();
        facture.setCheckInMontant(montantCheckIn);
        facture.setTax(TAX);
        facture.setStatus(PaiementStatus.EN_ATTENTE);
        facture.setType(FactureType.CHECK_IN);
        facture.setReservation(reservation);

            facture.setStatus(PaiementStatus.PAYE);
            factureRepository.save(facture);
            reservation.getFactureList().add(facture);
            reservationRepository.save(reservation);

    }

    @Override
    public double calculerMontantCheckIn(Reservation reservation) {
        double montantcheckIn = 0;
        long duree = Duration.between(reservation.getDateDebut().atStartOfDay(), reservation.getDateFin().atStartOfDay()).toDays();
        for (Chambre chambre : reservation.getChambreReservations().stream()
                .map(ChambreReservation::getChambre)
                .toList()) {
            montantcheckIn += chambre.getPrix();
        }
        double montantTotal = montantcheckIn * duree * (1 + TVA) + TAX;
        List<ReservationServices> servicesCheckin = reservationServiceRepository.findByReservationAndPhase(reservation.getId(), PhaseAjoutService.CHECK_IN);

        for (ReservationServices service : servicesCheckin) {
            montantTotal += service.getService().getPrix();
        }
        return montantTotal;
    }

    @Override
    public Boolean validerPaiementPaypal(double montant, Reservation reservation) {
        try {
            Map<String, String> sdkConfig = new HashMap<>();
            sdkConfig.put("mode", paypalMode);

            OAuthTokenCredential authTokenCredential = new OAuthTokenCredential(clientId, clientSecret, sdkConfig);
            String accessToken = authTokenCredential.getAccessToken();

            APIContext apiContext = new APIContext(accessToken);
            apiContext.setConfigurationMap(sdkConfig);

            Amount amount = new Amount();
            amount.setCurrency("MDH");
            amount.setTotal(String.format("%.2f", montant));

            Transaction transaction = new Transaction();
            transaction.setDescription("Paiement de check-in");
            transaction.setAmount(amount);

            List<Transaction> transactionList = new ArrayList<>();
            transactionList.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl("http://localhost:8080/paypal/cancel");
            redirectUrls.setReturnUrl("http://localhost:8080/paypal/return");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactionList);
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);

            return createdPayment != null && "created".equals(createdPayment.getState());

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Boolean payerFactureCheckIn(PaiementRequestDTO paiementRequest) {

        Reservation reservation = reservationRepository.findById(paiementRequest.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));


        double montantCheckIn = calculerMontantCheckIn(reservation);


        Facture facture = new Facture();
        facture.setCheckInMontant(montantCheckIn);
        facture.setTax(TAX);
        facture.setStatus(PaiementStatus.EN_ATTENTE);
        facture.setType(FactureType.CHECK_IN);
        facture.setReservation(reservation);


        boolean paymentStatus = false;

        if (PaiementMethod.STRIPE == PaiementMethod.valueOf(paiementRequest.getMethod())) {

            paymentStatus = validerPaiementStripe(paiementRequest);
        }


        if (paymentStatus) {
            facture.setStatus(PaiementStatus.PAYE);
            factureRepository.save(facture);

            reservation.getFactureList().add(facture);

            for (ReservationServices service : reservation.getServiceList()) {
                service.setPaiementStatus(PaiementStatus.PAYE);
                reservationServiceRepository.save(service);
            }

            reservationRepository.save(reservation);
        }

        return paymentStatus;
    }

    public boolean validerPaiementStripe(PaiementRequestDTO paiementRequest) {
        try {
            // Appeler l'API Stripe pour valider le paiement avec le clientSecret
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paiementRequest.getClientSecret());

            // Vérifier si le paiement a été validé
            return "succeeded".equals(paymentIntent.getStatus());
        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void payerFactureCheckInCache(Reservation reservation) {



        double montantCheckIn = calculerMontantCheckIn(reservation);


        Facture facture = new Facture();
        facture.setCheckInMontant(montantCheckIn);
        facture.setTax(TAX);
        facture.setStatus(PaiementStatus.PAYE);
        facture.setType(FactureType.CHECK_IN);
        facture.setReservation(reservation);

            factureRepository.save(facture);

            reservation.getFactureList().add(facture);

            for (ReservationServices service : reservation.getServiceList()) {
                service.setPaiementStatus(PaiementStatus.PAYE);
                reservationServiceRepository.save(service);
            }
        reservation.setStatus(ReservationStatus.CONFIRMEE);
            reservationRepository.save(reservation);
        }

    @Override
    public Facture validerPaiementCheckOut(Reservation reservation, double total) {
        try {
            Facture facture = Facture.builder()
                    .type(FactureType.CHECK_OUT)
                    .status(PaiementStatus.PAYE)
                    .checkOutMontant(total)
                    .reservation(reservation)
                    .build();

            return factureRepository.save(facture);
        }catch(Exception e) {
            e.printStackTrace();
            throw new PaymentValidationException("Failed to validate the checout payment");
        }
    }


}

