package com.danzabarr.messager.client;

import com.danzabarr.messager.core.*;
import org.mindrot.main.BCrypt;

import javax.net.SocketFactory;
import java.io.IOException;
import java.util.Scanner;

//java -cp out/production/Messager;lib/* com/danzabarr/messager/client/Client 127.0.0.1 8888
public class Client implements ConnectionListener
{
    private static Client client;

    public static Client instance() { return client; }

    public static Client createConnection(String host, int port)
            throws IOException
    {
        if (client == null)
        {
            client = new Client();
            client.connect(host, port);
        }
        return client;
    }

    private Client() {}

    //public static final String SERVER_ADDRESS = "127.0.0.1";//"2.24.151.114";
    //public static final int PORT = 8888;
    public static final String CLIENT_SALT = "$2a$10$Ei3R6HThMZQ2n4sGnLbC/e";

    private Connection connection;

    public void addListener(ConnectionListener listener)
    {
        connection.addListener(listener);
    }

    public static void main(String[] args)
    {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try
        {
            createConnection(host, port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }

        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            String input = scanner.nextLine();

            try
            {
                client.sendInput(input);
            }
            catch (InvalidArgumentCountException e)
            {
                e.printStackTrace();
            }
            catch (CommandNotRecognisedException e)
            {
                e.printStackTrace();
            }
            catch (NoLeadingSlashException e)
            {
                e.printStackTrace();
            }
            catch (UnencryptedConnectionException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sign(Request request)
    {
        request.sign(connection.getLocalSocketAddress().toString());
    }

    public void sendRequest(Request request, Connection.Encryption encryption)
            throws UnencryptedConnectionException
    {
        sign(request);
        connection.sendRequest(request, encryption);
    }

    public void sendInput(String input)
        throws
            InvalidArgumentCountException,
            CommandNotRecognisedException,
            NoLeadingSlashException,
            UnencryptedConnectionException
    {
        if (input == null)
            return;

        //Client side command pre-parsing
        Command command = CommandParser.parse(input);

        if (command.pattern.name.equals("login") || command.pattern.name.equals("register"))
        {
            String username = command.arguments[0];
            String password = command.arguments[1];
            String hashedPassword = BCrypt.hashpw(password, CLIENT_SALT);

            input = "/" + command.pattern.name + " " + username + " " + hashedPassword;
        }

        sendRequest(input, Connection.Encryption.RSA_AES);
    }

    public boolean isConnected()
    {
        return connection != null && connection.isConnected();
    }

    public void sendRequest(String data, Connection.Encryption encryption)
            throws UnencryptedConnectionException
    {
        Request request = new Request(data);
        sign(request);
        connection.sendRequest(request, encryption);
    }

    public void connect(String host, int port) throws IOException
    {
        if (connection != null)
            disconnect();

        SocketFactory factory = SocketFactory.getDefault();
        connection = new Connection(factory.createSocket(host, port));
        connection.addListener(this);
        connection.start();
    }

    public void disconnect()
    {
        if (connection == null)
            return;

        connection.disconnect();
        connection = null;
    }

    @Override
    public void onConnect(Connection connection)
    {
        System.out.println("Connected");
    }

    @Override
    public void onDisconnect(Connection connection)
    {
        System.out.println("Disconnected");
    }

    @Override
    public void onReceiveObject(Connection connection, Object obj)
    {
        System.out.println(obj);
    }

    @Override
    public void onReceiveRequest(Connection connection, Request request)
    {
        //handle request
        System.out.println(request);
    }
}
