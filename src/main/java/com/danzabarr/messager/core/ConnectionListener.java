package com.danzabarr.messager.core;

public interface ConnectionListener
{
    void onReceiveObject(Connection connection, Object obj);

    void onReceiveRequest(Connection connection, Request request);

    void onConnect(Connection connection);

    void onDisconnect(Connection connection);

}
