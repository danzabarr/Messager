package com.danzabarr.messager.core;

public class CommandLineException extends Exception
{
    public String input;

    public CommandLineException(String error, String input)
    {
        super(error);
        this.input = input;
    }
}
