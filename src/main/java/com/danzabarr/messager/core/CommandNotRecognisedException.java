package com.danzabarr.messager.core;

public class CommandNotRecognisedException extends Exception
{
    public final String name;

    public CommandNotRecognisedException(String name)
    {
        super("Command not recognised.");
        this.name = name;
    }

}
