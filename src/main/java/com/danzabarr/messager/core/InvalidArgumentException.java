package com.danzabarr.messager.core;

public class InvalidArgumentException extends Exception
{
    public final int argumentIndex;
    public final String argumentName;
    public final String argumentType;

    public InvalidArgumentException(int argumentIndex, String argumentName, String argumentType)
    {
        super(generateErrorMessage(argumentIndex, argumentName, argumentType));
        this.argumentIndex = argumentIndex;
        this.argumentName = argumentName;
        this.argumentType = argumentType;
    }

    public static String generateErrorMessage(int argumentIndex, String argumentName, String argumentType)
    {
        return "Invalid argument " + argumentIndex + " " + argumentName + " is of type " + argumentType;
    }
}
