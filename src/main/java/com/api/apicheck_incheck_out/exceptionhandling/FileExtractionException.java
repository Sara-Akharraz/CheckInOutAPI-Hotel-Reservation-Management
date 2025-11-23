package com.api.apicheck_incheck_out.exceptionhandling;

public class FileExtractionException extends RuntimeException{
    public FileExtractionException(String message,Throwable cause){
        super(message,cause);
    }
}
