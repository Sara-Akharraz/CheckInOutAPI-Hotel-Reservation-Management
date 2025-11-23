package com.api.apicheck_incheck_out.exceptionhandling;

public class ServiceNotFoundException extends RuntimeException{
    public ServiceNotFoundException(String message){
        super(message);
    }
}
