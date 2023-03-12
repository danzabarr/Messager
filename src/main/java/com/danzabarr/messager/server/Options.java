package com.danzabarr.messager.server;

public class Options
{
    private boolean localhost = false;
    private int port = 8888;

    public static Options validateCommandLineArgs(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            throw new Exception("Error validating command line arguments. Invalid number of arguments. Usage: MessagerServer <localhost> <port>");
        }

        boolean localhost = false;
        int port = 0;

        localhost = Boolean.parseBoolean(args[0]);

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

        return new Options
                (
                        localhost,
                        port
                );
    }

    private Options(boolean localhost, int port)
    {
        this.localhost = localhost;
        this.port = port;
    }

    public boolean isLocalhost()
    {
        return localhost;
    }

    public int getPort()
    {
        return port;
    }
}
