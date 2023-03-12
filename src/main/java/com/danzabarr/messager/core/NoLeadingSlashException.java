package com.danzabarr.messager.core;

public class NoLeadingSlashException extends Exception
{
    public NoLeadingSlashException()
    {
        super("Invalid command. Commands must start with a forward slash '/'.");
    }
}
