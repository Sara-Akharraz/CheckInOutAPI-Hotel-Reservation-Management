package com.api.apicheck_incheck_out.exceptionhandling;

public class InvalidReservationStatusException extends RuntimeException{
    public InvalidReservationStatusException(String message){
        super(message);
    }
}
