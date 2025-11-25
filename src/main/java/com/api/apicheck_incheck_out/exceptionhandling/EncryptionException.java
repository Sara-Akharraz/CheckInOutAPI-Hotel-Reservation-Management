package com.api.apicheck_incheck_out.exceptionhandling;

public class EncryptionException extends RuntimeException{
    public EncryptionException(Throwable cause){
        super(cause);
    }
}
