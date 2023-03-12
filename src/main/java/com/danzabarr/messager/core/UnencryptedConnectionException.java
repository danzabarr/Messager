package com.danzabarr.messager.core;

public class UnencryptedConnectionException extends Exception
{
    public Connection connection;

    public UnencryptedConnectionException(Connection connection)
    {
        super("Connection " + connection + " is not encrypt-ready.");
        this.connection = connection;
    }

}
