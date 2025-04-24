package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;

public interface FactureService {


    //Boolean payerFactureCheckIn(Reservation reservation, PaiementMethod method);

//    public double calculerMontantCheckIn(Reservation reservation);
//    public Boolean validerPaiementStripe(double montant, Reservation reservation);
//    public Boolean validerPaiementPaypal(double montant, Reservation reservation);
    public Facture validerPaiementCheckOut(Reservation reservation);
}