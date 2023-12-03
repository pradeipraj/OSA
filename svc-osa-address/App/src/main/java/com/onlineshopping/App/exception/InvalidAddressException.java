package com.onlineshopping.App.exception;

public class InvalidAddressException extends RuntimeException{
    public InvalidAddressException(String message){
        super(message);
    }
}
