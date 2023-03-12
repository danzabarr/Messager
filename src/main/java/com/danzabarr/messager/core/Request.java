package com.danzabarr.messager.core;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Request implements Serializable
{
    public final String input;

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public String getSender()
    {
        return sender;
    }

    private Instant timestamp;
    private String sender;
    public String senderUsername;
    private boolean signed;

    public boolean isSigned()
    {
        return signed;
    }

    public Request(String input)
    {
        this.input = input;
    }

    public void sign(String sender)
    {
        this.sender = sender;
        timestamp = Instant.now();
        signed = true;
    }

    public String getTimestampString()
    {
        return DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault()).format(timestamp);
    }

    public String getSenderString()
    {
        return senderUsername == null ? sender : senderUsername;
    }

    public String getTextString()
    {
        return input;
    }

    public String toString()
    {
        String str = "";
        if (timestamp != null)
            str += DateTimeFormatter.ofPattern("HH:mm:ss ").withZone(ZoneId.systemDefault()).format(timestamp);

        if (senderUsername != null)
            str += "[" + senderUsername + "]: ";

        else if (sender != null)
            str += "[" + sender + "]: ";

        str += input;

        return str;
    }
}
