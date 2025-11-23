package com.api.apicheck_incheck_out.exceptionhandling;

public class InvalidPassportException extends RuntimeException{
    public InvalidPassportException(String message){
        super(message);
    }
}
