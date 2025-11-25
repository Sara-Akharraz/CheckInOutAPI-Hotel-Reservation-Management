package com.api.apicheck_incheck_out.exceptionhandling;

public class PDFGenerationException extends RuntimeException{
    public PDFGenerationException(String message,Throwable cause){
        super(message,cause);
    }
}
