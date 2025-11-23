package com.api.apicheck_incheck_out.exceptionhandling;

public class ChambreReservationNotFoundException extends RuntimeException{
    public ChambreReservationNotFoundException(String message){
        super(message);
    }
}
