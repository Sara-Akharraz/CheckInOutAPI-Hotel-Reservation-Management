package com.api.apicheck_incheck_out.exceptionhandling;

public class UserRegistrationException extends RuntimeException{
    public UserRegistrationException(String message,Throwable cause){
        super(message,cause);
    }
}
