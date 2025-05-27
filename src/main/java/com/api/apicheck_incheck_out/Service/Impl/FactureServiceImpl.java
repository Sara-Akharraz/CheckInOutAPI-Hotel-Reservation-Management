package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DTO.PaiementRequestDTO;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.*;
import com.api.apicheck_incheck_out.Repository.FactureRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.Service.FactureService;
import com.api.apicheck_incheck_out.Stripe.Service.Impl.StripeServiceImpl;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FactureServiceImpl implements FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationServiceRepository reservationServiceRepository;
    @Autowired
    private StripeServiceImpl stripeService;


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
    public void payerFactureCheckIn(Reservation reservation) {

        double montantCheckIn = calculerMontantCheckIn(reservation);

        Facture facture = new Facture();
        facture.setCheckInMontant(montantCheckIn);
        facture.setTax(tax);
        facture.setStatus(PaiementStatus.en_attente);
        facture.setType(FactureType.Check_In);
        facture.setReservation(reservation);

            facture.setStatus(PaiementStatus.paye);
            factureRepository.save(facture);
            reservation.getFactureList().add(facture);
            reservationRepository.save(reservation);

    }

    @Override
    public double calculerMontantCheckIn(Reservation reservation) {
        double montantcheckIn = 0;
        long duree = Duration.between(reservation.getDate_debut().atStartOfDay(), reservation.getDate_fin().atStartOfDay()).toDays();
        for (Chambre chambre : reservation.getChambreReservations().stream()
                .map(ChambreReservation::getChambre)
                .collect(Collectors.toList())) {
            montantcheckIn += chambre.getPrix();
        }
        double montantTotal = montantcheckIn * duree * (1 + tva) + tax;
        List<ReservationServices> servicesCheckin = reservationServiceRepository.findByReservationAndPhase(reservation.getId(), PhaseAjoutService.check_in);

        for (ReservationServices service : servicesCheckin) {
            montantTotal += service.getService().getPrix();
        }
        return montantTotal;
    }

    //    @Override
//    public Boolean validerPaiementStripe(double montant, Reservation reservation) {
//
//        try {
//            return stripeService.processPayment(
//                    montant,
//                    reservation.getUser().getStripeId(),
//                    "MAD"
//            );
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
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
        facture.setTax(tax);
        facture.setStatus(PaiementStatus.en_attente);
        facture.setType(FactureType.Check_In);
        facture.setReservation(reservation);


        boolean paymentStatus = false;

        if (PaiementMethod.STRIPE.equals(paiementRequest.getMethod())) {

            paymentStatus = validerPaiementStripe(paiementRequest);
        }

        // else if (PaiementMethod.PAYPAL.equals(paiementRequest.getMethod())) {
        //     paymentStatus = validerPaiementPaypal(montantCheckIn, reservation);
        // }

        if (paymentStatus) {
            facture.setStatus(PaiementStatus.paye);
            factureRepository.save(facture);

            reservation.getFactureList().add(facture);

            for (ReservationServices service : reservation.getServiceList()) {
                service.setPaiementStatus(PaiementStatus.paye);
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
            if ("succeeded".equals(paymentIntent.getStatus())) {
                return true;
            } else {
                return false;
            }
        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }

//        public Boolean validerPaiementStripe (PaiementRequestDTO paiementRequest){
//            String cardNumber = paiementRequest.getCardNumber().replaceAll("\\s", "");
//
//            switch (cardNumber) {
//                case "4242424242424242":
//                    System.out.println("Paiement réussi");
//                    return true;
//                case "4000000000000069":
//                    throw new RuntimeException("Échec du paiement, carte expirée");
//                case "4000000000009995":
//                    throw new RuntimeException("Échec du paiement, fonds insuffisants");
//                case "4000000000000002":
//                    throw new RuntimeException("Échec du paiement, carte signalée pour fraude");
//                case "4000000000000044":
//                    throw new RuntimeException("Échec du paiement, code postal invalide");
//                case "4000000000000036":
//                    throw new RuntimeException("Échec du paiement, erreur de réseau");
//                default:
//                    throw new RuntimeException("Paiement échoué, numéro de carte invalide");
//            }
//        }


    @Override
    public void payerFactureCheckInCache(Reservation reservation) {



        double montantCheckIn = calculerMontantCheckIn(reservation);


        Facture facture = new Facture();
        facture.setCheckInMontant(montantCheckIn);
        facture.setTax(tax);
        facture.setStatus(PaiementStatus.paye);
        facture.setType(FactureType.Check_In);
        facture.setReservation(reservation);

            factureRepository.save(facture);

            reservation.getFactureList().add(facture);

            for (ReservationServices service : reservation.getServiceList()) {
                service.setPaiementStatus(PaiementStatus.paye);
                reservationServiceRepository.save(service);
            }
        reservation.setStatus(ReservationStatus.Confirmee);
            reservationRepository.save(reservation);
        }

    @Override
    public Facture validerPaiementCheckOut(Reservation reservation, double total) {
        try {
            Facture facture = Facture.builder()
                    .type(FactureType.Check_Out)
                    .status(PaiementStatus.paye)
                    .checkOutMontant(total)
                    .reservation(reservation)
                    .build();

            return factureRepository.save(facture);
        }catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to validate the checout payment");
        }
    }


}

