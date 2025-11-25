package com.api.apicheck_incheck_out.exceptionhandling;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
