package com.api.apicheck_incheck_out.exceptionhandling;

public class CheckInNotFoundException extends RuntimeException {
    public CheckInNotFoundException(String message){
        super(message);
    }
}
