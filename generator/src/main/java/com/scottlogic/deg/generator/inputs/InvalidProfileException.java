package com.scottlogic.deg.generator.inputs;

public class InvalidProfileException extends Exception
{
    public InvalidProfileException(String message) {
        super(message);
    }

    public InvalidProfileException(Exception e) {
        super(e);
    }
}
