package com.api.apicheck_incheck_out.exceptionhandling;

public class ChambreNotFoundException extends RuntimeException{
    public ChambreNotFoundException(String message){
        super(message);
    }
}
