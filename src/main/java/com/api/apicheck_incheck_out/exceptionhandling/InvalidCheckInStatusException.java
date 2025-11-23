package com.api.apicheck_incheck_out.exceptionhandling;

public class InvalidCheckInStatusException extends RuntimeException{
    public InvalidCheckInStatusException(String message){
        super(message);
    }
}
