package com.api.apicheck_incheck_out.exceptionhandling;

public class InvalidCINException extends RuntimeException{
    public InvalidCINException(String message){
        super(message);
    }
}
