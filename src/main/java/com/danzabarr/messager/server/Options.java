package com.danzabarr.messager.server;

public class Options
{
    public final boolean localhost;
    public final int port;

    public final String db_hostname;
    public final int db_port;
    public final String db_database;
    public final String db_username;
    public final String db_password;

    public static Options validateCommandLineArgs(String[] args) throws Exception
    {
        if (args.length != 7)
        {
            throw new Exception("Error validating command line arguments. Invalid number of arguments. Usage: MessagerServer <localhost> <port>");
        }

        boolean localhost = Boolean.parseBoolean(args[0]);
        int port = 0;

        try
        {
            port = Integer.parseInt(args[1]);
            if (port < 0 || port > 65535)
            {
                throw new Exception("Error validating command line arguments. Invalid port number (" + args[0] + "): port number must be between 0-65535.");
            }
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Error validating command line arguments. Invalid port number (" + args[0] + "): could not parse argument to an integer.");
        }

        String db_hostname = args[2];
        int db_port = Integer.parseInt(args[3]);
        String db_database = args[4];
        String db_username = args[5];
        String db_password = args[6];

        return new Options
                (
                        localhost,
                        port,
                        db_hostname,
                        db_port,
                        db_database,
                        db_username,
                        db_password
                );
    }

    private Options(boolean localhost, int port, String db_hostname, int db_port, String db_database, String db_username, String db_password)
    {
        this.localhost = localhost;
        this.port = port;
        this.db_hostname = db_hostname;
        this.db_port = db_port;
        this.db_database = db_database;
        this.db_username = db_username;
        this.db_password = db_password;
    }
}
