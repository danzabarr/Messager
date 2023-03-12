package com.danzabarr.messager.core;

public class InsufficientPermissionsException extends Exception
{
    public InsufficientPermissionsException()
    {
        super("Insufficient permission level to perform that action.");
    }
}
