package com.api.apicheck_incheck_out.exceptionhandling;

public class ReservationNotFoundException extends RuntimeException{
    public ReservationNotFoundException(String message){
        super(message);
    }
}
