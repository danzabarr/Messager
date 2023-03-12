package com.danzabarr.messager.core;

import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.ArrayList;

public class Connection extends Thread
{
    public enum State
    {
        UNINITIALISED,
        CONNECTED,
        ENCRYPT_READY,
        LOGGED_IN,
        DISCONNECTED
    }

    protected Socket socket;
    private ArrayList<ConnectionListener> listeners = new ArrayList<>();

    private State state = State.UNINITIALISED;

    private RSAPublicKey publicKey;
    private RSAPublicKey remotePublicKey;
    private RSAPrivateKey privateKey;

    private Cipher rsaEncrypter;
    private Cipher rsaDecrypter;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Connection(Socket socket)
    {
        this.socket = socket;
        if (socket.isConnected())
            state = State.CONNECTED;
    }

    public boolean isConnected()
    {
        return socket.isConnected();
    }

    public String toString()
    {
        return socket.getRemoteSocketAddress().toString();
    }

    public SocketAddress getRemoteSocketAddress()
    {
        return socket.getRemoteSocketAddress();
    }

    public SocketAddress getLocalSocketAddress()
    {
        return socket.getLocalSocketAddress();
    }

    public void addListener(ConnectionListener listener)
    {
        if (listener == null)
            return;

        if (listeners.contains(listener))
            return;

        listeners.add(listener);
    }

    public void removeListener(ConnectionListener listener)
    {
        listeners.remove(listener);
    }

    protected void notifyReceiveObject(Object object)
    {
        for (ConnectionListener listener : listeners)
            listener.onReceiveObject(this, object);
    }

    protected void notifyReceiveRequest(Request request)
    {
        for (ConnectionListener listener : listeners)
            listener.onReceiveRequest(this, request);
    }

    protected void notifyConnect()
    {
        for (ConnectionListener listener : listeners)
            listener.onConnect(this);
    }

    protected void notifyDisconnect()
    {
        for (ConnectionListener listener : listeners)
            listener.onDisconnect(this);
    }

    @Override
    public void run()
    {
        try
        {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);

            KeyPair kp = kpg.genKeyPair();
            publicKey = (RSAPublicKey) kp.getPublic();
            privateKey = (RSAPrivateKey) kp.getPrivate();

            rsaDecrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaDecrypter.init(Cipher.DECRYPT_MODE, privateKey);

            sendPublicKey();

            notifyConnect();

            Object input = in.readObject();
            while (input != null)
            {
                if (input instanceof RSAPublicKey)
                {
                    receivePublicKey((RSAPublicKey) input);
                }
                else if (input instanceof Packet)
                {
                    Packet packet = (Packet) input;
                    Request decrypted = Packet.decrypt(packet, rsaDecrypter);

                    notifyReceiveRequest(decrypted);
                }
                else if (input instanceof SealedObject)
                {
                    SealedObject encrypted = (SealedObject) input;
                    Object decrypted = encrypted.getObject(rsaDecrypter);

                    if (decrypted instanceof Request)
                        notifyReceiveRequest((Request) decrypted);
                    else
                        notifyReceiveObject(decrypted);
                }
                else
                {
                    notifyReceiveObject(input);
                }

                input = in.readObject();
            }
        }
        catch (Exception e)
        {
            //System.err.println(e.getMessage());
            e.printStackTrace();
        }

        disconnect();
    }

    public enum Encryption
    {
        None,
        RSA,
        RSA_AES
    }

    public void sendRequest(Request request, Encryption encryption)
            throws UnencryptedConnectionException
    {
        switch (encryption)
        {
            case None:
                try
                {
                    out.writeObject(request);
                    out.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;

            case RSA:

                if (rsaEncrypter == null)
                    throw new UnencryptedConnectionException(this);

                try
                {
                    SealedObject encrypted = new SealedObject(request, rsaEncrypter);
                    out.writeObject(encrypted);
                    out.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalBlockSizeException e)
                {
                    e.printStackTrace();
                }
                break;

            case RSA_AES:

                if (rsaEncrypter == null)
                    throw new UnencryptedConnectionException(this);

                try
                {
                    Packet encryptedPacket = Packet.encrypt(request, rsaEncrypter);
                    out.writeObject(encryptedPacket);
                    out.flush();
                }
                catch (NoSuchAlgorithmException e)
                {
                    e.printStackTrace();
                }
                catch (NoSuchPaddingException e)
                {
                    e.printStackTrace();
                }
                catch (InvalidKeyException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalBlockSizeException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void sendPublicKey() throws IOException
    {
        out.writeObject(publicKey);
        out.flush();
    }

    public void receivePublicKey(RSAPublicKey key)
    {
        if (key == null)
            return;

        if (state == State.UNINITIALISED)
            return;

        if (state == State.DISCONNECTED)
            return;

        try
        {
            remotePublicKey = key;
            rsaEncrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaEncrypter.init(Cipher.ENCRYPT_MODE, remotePublicKey);
            state = State.ENCRYPT_READY;
        }
        catch (GeneralSecurityException e)
        {
            e.printStackTrace();
            remotePublicKey = null;
            rsaEncrypter = null;
        }
    }

    public void disconnect()
    {
        try
        {
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException e)
        {

        }
        catch (NullPointerException e)
        {

        }
        notifyDisconnect();
    }
}
