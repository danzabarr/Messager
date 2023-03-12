package com.danzabarr.messager.core;

public class LoginFailedException extends Exception
{
    public final String username;
    public final String password;

    public LoginFailedException(String username, String password, String reason)
    {
        super(reason);
        this.username = username;
        this.password = password;
    }
}
