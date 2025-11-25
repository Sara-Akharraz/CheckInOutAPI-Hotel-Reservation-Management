package com.api.apicheck_incheck_out.exceptionhandling;

public class PaymentValidationException extends RuntimeException{
    public PaymentValidationException(String message){
        super(message);
    }
}
