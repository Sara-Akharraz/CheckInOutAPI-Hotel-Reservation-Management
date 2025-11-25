package com.api.apicheck_incheck_out.exceptionhandling;

public class InvalidNameException extends RuntimeException{
    public InvalidNameException(String message){
        super(message);
    }
}
