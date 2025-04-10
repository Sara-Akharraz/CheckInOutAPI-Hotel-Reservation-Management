package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.FactureType;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Repository.FactureRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FactureServiceImpl implements FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    private static final String STRIPE_SECRET_KEY = "a_sercret_key";
    private static final double tva = 0.2;
    private static final double tax = 10;

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String paypalMode;

    @Override
    public Boolean payerFactureCheckIn(Reservation reservation, PaiementMethod method) {

        double montantCheckIn = calculerMontantCheckIn(reservation);

        Facture facture = new Facture();
        facture.setCheckInMontant(montantCheckIn);
        facture.setTax(tax);
        facture.setStatus(PaiementStatus.en_attente);
        facture.setType(FactureType.Check_In);
        facture.setReservation(reservation);
        facture = factureRepository.save(facture);

        boolean paymentStatus = true;

        if (method.equals(PaiementMethod.STRIPE)) {
            paymentStatus = validerPaiementStripe(montantCheckIn, reservation);
        } else if (method.equals(PaiementMethod.PAYPAL)) {
            paymentStatus = validerPaiementPaypal(montantCheckIn, reservation);
        }
        paymentStatus = true;
        if (paymentStatus) {
            facture.setStatus(PaiementStatus.paye);
            factureRepository.save(facture);
            reservation.getFactureList().add(facture);
            reservationRepository.save(reservation);
        }

        return true;
    }

    @Override
    public double calculerMontantCheckIn(Reservation reservation) {
        double montantcheckIn = 0;
        for (Chambre chambre : reservation.getChambreList()) {
            montantcheckIn += chambre.getPrix();
        }
        return montantcheckIn * (1 + tva) + tax;
    }

    @Override
    public Boolean validerPaiementStripe(double montant, Reservation reservation) {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) montant)
                    .setCurrency("MAD")
                    .setCustomer(reservation.getUser().getStripeId())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return "succeeded".equals(paymentIntent.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
}
