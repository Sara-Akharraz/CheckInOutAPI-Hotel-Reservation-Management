package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.dto.PaiementRequestDTO;
import com.api.apicheck_incheck_out.entity.Facture;
import com.api.apicheck_incheck_out.entity.Reservation;

public interface FactureService {


    void payerFactureCheckIn(Reservation reservation);
    public double calculerMontantCheckIn(Reservation reservation);

    public Boolean payerFactureCheckIn(PaiementRequestDTO paiementRequest);
    public boolean validerPaiementStripe (PaiementRequestDTO paiementRequest);
    public void payerFactureCheckInCache(Reservation reservation);
    public Facture validerPaiementCheckOut(Reservation reservation, double total);

}
