package com.danzabarr.messager.core;

import javax.crypto.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class Packet implements Serializable
{
    private SealedObject encryptedRequest;
    private SealedObject encryptedKey;

    private Packet(SealedObject encryptedRequest, SealedObject encryptedKey)
    {
        this.encryptedRequest = encryptedRequest;
        this.encryptedKey = encryptedKey;
    }

    public static Packet encrypt(Request request, Cipher rsaEncrypter)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        SealedObject aesEncryptedData = new SealedObject(request, aesCipher);
        SealedObject rsaEncryptedKey = new SealedObject(secretKey, rsaEncrypter);

        Packet packet = new Packet(aesEncryptedData, rsaEncryptedKey);

        return packet;
    }

    public static Packet encrypt(Request request, RSAPublicKey remotePublicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException
    {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, remotePublicKey);

        return encrypt(request, rsaCipher);
    }

    public static Request decrypt(Packet packet, Cipher rsaDecrypter)
            throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            IOException,
            BadPaddingException,
            ClassNotFoundException,
            ClassCastException
    {
        SecretKey decryptedKey = (SecretKey) packet.encryptedKey.getObject(rsaDecrypter);

        Cipher aesDecrypter = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        aesDecrypter.init(Cipher.DECRYPT_MODE, decryptedKey);

        Request decryptedRequest = (Request) packet.encryptedRequest.getObject(aesDecrypter);

        return decryptedRequest;
    }

    public static Request decrypt(Packet packet, RSAPrivateKey privateKey)
        throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            IOException,
            BadPaddingException,
            ClassNotFoundException,
            ClassCastException
    {
        Cipher rsaDecrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaDecrypter.init(Cipher.DECRYPT_MODE, privateKey);

        return decrypt(packet, rsaDecrypter);
    }
}
