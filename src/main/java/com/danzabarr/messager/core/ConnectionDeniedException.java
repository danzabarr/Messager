package com.danzabarr.messager.core;

import java.net.Socket;

public class ConnectionDeniedException extends Exception
{
    public final Socket socket;
    public ConnectionDeniedException(Socket socket, String reason)
    {
        super("Connection was refused. " + reason);
        this.socket = socket;
    }
}
