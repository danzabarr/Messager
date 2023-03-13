package com.danzabarr.messager.server;

import com.danzabarr.messager.core.*;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * TO RUN SERVER
 * cd IdeaProjects\Messager
 * java -cp out/production/Messager;lib/* com/danzabarr/messager/server/Server 8888
 */

public class Server extends Thread implements ConnectionListener
{
    public static void main(String[] args)
    {
        Options options = null;
        try
        {
            options = Options.validateCommandLineArgs(args);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }

        Server server = new Server(options);
        try
        {
            server.connectDatabase(options.db_hostname, options.db_port, options.db_database, options.db_username, options.db_password);
            server.startServer(options.localhost);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private ServerSocket serverSocket;
    private HashMap<Connection, User> connections = new HashMap<>();
    private Database db;
    private final Options options;
    private boolean running = false;

    private Server(Options options)
    {
        this.options = options;
    }

    public void startServer(boolean localhost) throws IOException
    {
        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        System.out.println("Starting server on port: " + options.port);
        serverSocket = localhost
                ? factory.createServerSocket(options.port, 0, InetAddress.getLoopbackAddress())
                : factory.createServerSocket(options.port);

        start();
    }

    public void stopServer()
    {
        running = false;
        interrupt();
    }

    @Override
    public void run()
    {
        running = true;
        while (running)
        {
            try
            {
                // BLOCKING
                Socket socket = serverSocket.accept();

                approveConnection(socket);

                Connection connection = new Connection(socket);

                connection.addListener(this);
                connections.put(connection, null);

                connection.start();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (ConnectionDeniedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void approveConnection(Socket socket)
            throws ConnectionDeniedException
    {

    }

    @Override
    public void onReceiveObject(Connection connection, Object object)
    {

    }

    @Override
    public void onReceiveRequest(Connection connection, Request request)
    {

        try
        {
            handleRequest(connection, request);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            Request errorPacket = new Request(e.getMessage());
            sign(errorPacket);
            try
            {
                connection.sendRequest(errorPacket, Connection.Encryption.RSA_AES);
            }
            catch (UnencryptedConnectionException ex)
            {
                ex.printStackTrace();
                //Probably kick this connection.
            }
        }
    }

    private void authenticateCommand(Connection connection, User user, Command command)
            throws InsufficientPermissionsException
    {

    }

    private void handleRequest(Connection connection, Request request)
            throws
            SQLException,
            LoginFailedException,
            InsufficientPermissionsException,
            InvalidArgumentCountException,
            CommandNotRecognisedException,
            NoLeadingSlashException,
            InvalidArgumentException
    {
        Command command = CommandParser.parse(request);

        User user = name(connection, request);

        System.out.println(request);

        authenticateCommand(connection, user, command);

        switch (command.pattern.name)
        {
            case "say":

                for (Connection c : connections.keySet())
                {
                    try
                    {
                        c.sendRequest(request, Connection.Encryption.RSA_AES);
                    }
                    catch (UnencryptedConnectionException e)
                    {
                        //e.printStackTrace();
                    }
                }

                break;

            case "login":
            {
                String username = command.arguments[0];
                String password = command.arguments[1];
                handleLoginRequest(connection, username, password);
            }
            break;

            case "logout":
                handleLogoutRequest(connection);

                break;

            case "register":
            {
                String username = command.arguments[0];
                String password = command.arguments[1];
                handleRegistrationRequest(connection, username, password);
            }
            default:

                break;
        }
    }

    private void sign(Request request)
    {
        request.sign(serverSocket.getLocalSocketAddress().toString());
        request.senderUsername = "Server";
    }

    private User name(Connection connection, Request request)
    {
        User user = null;

        if (connections.containsKey(connection))
        {
            user = connections.get(connection);
            if (user != null)
                request.senderUsername = user.username;
        }

        return user;
    }

    @Override
    public void onConnect(Connection connection)
    {
        String name = name(connection);
        System.out.println(name + " connected.");

        broadcast(name + " connected.");
    }

    @Override
    public void onDisconnect(Connection connection)
    {
        String name = name(connection);
        System.out.println(name + " disconnected.");

        connections.remove(connection);

        broadcast(name + " disconnected.");
    }

    public String name(Connection connection)
    {
        User user = null;

        if (connections.containsKey(connection))
            user = connections.get(connection);

        return user == null ? connection.getRemoteSocketAddress().toString() : user.username;
    }

    public void broadcast(String message)
    {
        Request request = new Request(message);
        sign(request);
        System.out.println(request);
        for (Connection c : connections.keySet())
        {
            try
            {
                c.sendRequest(request, Connection.Encryption.RSA_AES);
            }
            catch (UnencryptedConnectionException e)
            {
                //e.printStackTrace();
            }
        }
    }

    public Connection getConnectionFromIP(String ip)
            throws Exception
    {
        for (Connection connection : connections.keySet())
        {

            if (connection.getRemoteSocketAddress().toString().equals(ip))
                return connection;
        }
        throw new Exception("A user with that IP address is not online.");
    }

    public Connection getConnectionFromUsername(String username)
            throws Exception
    {

        for (Map.Entry<Connection, User> keypair : connections.entrySet())
        {
            Connection connection = keypair.getKey();
            User user = keypair.getValue();

            if (user == null)
                continue;

            if (user.username.equals(username))
                return connection;
        }

        throw new Exception("A user with that name is not online.");
    }

    public void informUser(String message, String username)
            throws Exception
    {
        Connection connection = getConnectionFromUsername(username);
        inform(message, connection);
    }

    public void informIPAddress(String message, String ip)
            throws Exception
    {
        Connection connection = getConnectionFromIP(ip);
        inform(message, connection);
    }

    public void inform(String message, Connection connection)
            throws UnencryptedConnectionException
    {
        Request request = new Request(message);
        sign(request);
        connection.sendRequest(request, Connection.Encryption.RSA_AES);
    }

    public void connectDatabase(String host_name, int port, String database, String user_name, String password)
            throws SQLException
    {
        db = new Database(host_name, port, database, user_name, password);
    }

    private void handleLogoutRequest(Connection connection)
    {
        broadcast(name(connection) + " logged out.");
        connections.put(connection, null);
    }

    private User handleLoginRequest(Connection connection, String username, String password)
            throws SQLException, LoginFailedException
    {
        User user = db.getUserCheckPassword(username, password);

        if (connections.containsValue(user))
            throw new LoginFailedException(username, password, "User is already logged in.");

        connections.put(connection, user);

        broadcast(username + " logged in.");

        return user;
    }

    private User handleRegistrationRequest(Connection connection, String username, String password)
        throws
            SQLException,
            LoginFailedException
    {
        User user = db.registerUser(username, password);

        if (connections.containsValue(user))
            throw new LoginFailedException(username, password, "User is already logged in.");

        connections.put(connection, user);

        broadcast(username + " just joined!");

        return user;
    }
}
