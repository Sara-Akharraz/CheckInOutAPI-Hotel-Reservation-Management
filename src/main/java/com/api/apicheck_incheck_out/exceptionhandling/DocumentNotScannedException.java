package com.api.apicheck_incheck_out.exceptionhandling;

public class DocumentNotScannedException extends RuntimeException{
    public DocumentNotScannedException(String message){
        super(message);
    }
}
