package org.example.Client.exceptions;

public class NumberFormatException extends RuntimeException{
    public NumberFormatException(String massange){//При вводе 0
        super(massange);
    }
}
