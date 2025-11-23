package com.api.apicheck_incheck_out.exceptionhandling;

public class CheckOutNotFoundException extends RuntimeException{
    public CheckOutNotFoundException(String message){
        super(message);
    }
}
