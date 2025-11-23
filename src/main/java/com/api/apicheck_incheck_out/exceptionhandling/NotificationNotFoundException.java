package com.api.apicheck_incheck_out.exceptionhandling;

public class NotificationNotFoundException extends RuntimeException{
    public NotificationNotFoundException(String message){
        super(message);
    }
}
