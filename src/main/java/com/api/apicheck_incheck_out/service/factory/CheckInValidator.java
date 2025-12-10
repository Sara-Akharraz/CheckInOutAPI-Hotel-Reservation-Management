package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.exceptionhandling.DocumentNotScannedException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidCheckInStatusException;
import org.springframework.stereotype.Component;

@Component
public class CheckInValidator {
    public void validateForConfirmation(CheckIn checkIn){
        validateDocumentScanned(checkIn);
        validateStatusEnAttente(checkIn);
    }
    private void validateDocumentScanned(CheckIn checkIn){
        if(checkIn.getDocumentScan()==null){
        throw new DocumentNotScannedException("Le scan du document n'a pas été effectué.");
        }
    }
    private void validateStatusEnAttente(CheckIn checkIn){
        if(checkIn.getStatus()!= CheckInStatus.EN_ATTENTE){
            throw new InvalidCheckInStatusException("Le check-in n'est pas en attente.");
        }
    }
}
