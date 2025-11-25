package com.api.apicheck_incheck_out.exceptionhandling;

public class EmailAlreadyUsedException extends RuntimeException{
    public EmailAlreadyUsedException(String message){
        super(message);
    }
}
