package com.danzabarr.messager.core;

public class InvalidArgumentCountException extends Exception
{
    public final int argumentsSupplied;
    public final int argumentsRequired;

    public InvalidArgumentCountException(int argumentsSupplied, int argumentsRequired)
    {
        super(generateErrorMessage(argumentsSupplied, argumentsRequired));
        this.argumentsSupplied = argumentsSupplied;
        this.argumentsRequired = argumentsRequired;
    }

    public static String generateErrorMessage(int argumentsSupplied, int argumentsRequired)
    {
        return "Incorrect number of arguments, required " + argumentsRequired + " and " + argumentsSupplied + " arguments were supplied.";
    }
}
