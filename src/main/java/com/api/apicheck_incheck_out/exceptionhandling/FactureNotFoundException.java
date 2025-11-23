package com.api.apicheck_incheck_out.exceptionhandling;

public class FactureNotFoundException extends RuntimeException{
    public FactureNotFoundException(String message){
        super(message);
    }
}
