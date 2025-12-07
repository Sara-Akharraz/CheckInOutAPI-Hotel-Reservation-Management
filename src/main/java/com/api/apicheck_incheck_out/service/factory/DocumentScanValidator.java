package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidCINException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidNameException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidPassportException;
import org.springframework.stereotype.Component;

@Component
public class DocumentScanValidator {
    public void validateDocument(Reservation reservation, DocumentScanDTO doc){
        User user=reservation.getUser();
        validateName(user,doc);
        validateIdentificationDocument(user,doc);

    }
    private void validateName(User user,DocumentScanDTO doc){
        if(!user.getNom().equalsIgnoreCase(doc.getNom())||!user.getPrenom().equalsIgnoreCase(doc.getPrenom())){
            throw new InvalidNameException("Nom ou prénom incorrect !");
        }
    }
    private void validateIdentificationDocument(User user,DocumentScanDTO doc){
        if (doc.getType() == DocumentScanType.CIN) {
            validateCIN(user, doc);
        } else if (doc.getType() == DocumentScanType.PASSPORT) {
            validatePassport(user, doc);
        }
    }
    private void validateCIN(User user, DocumentScanDTO doc) {
        if (user.getCin() == null || !user.getCin().equalsIgnoreCase(doc.getCin())) {
            throw new InvalidCINException("CIN non valide !");
        }
    }
    private void validatePassport(User user, DocumentScanDTO doc) {
        if (user.getNumeroPassport() == null ||
                !user.getNumeroPassport().equalsIgnoreCase(doc.getPassport())) {
            throw new InvalidPassportException("Passport non valide !");
        }
    }
}
