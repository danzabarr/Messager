package com.danzabarr.messager.core;

import com.danzabarr.messager.server.User;

import java.util.HashMap;

/*
        /say Hello there                say message...                      Usage: /say <message>
        /tell Bob Hey bob               tell user:name string:message...    Usage: /tell <name> <message>
        /reply I agree                  reply string:message...             Usage: /reply <message>
        /login Danzabarr password       login user:name pass:password       Usage: /login <name> <password>
        /logout                         logout                              Usage: /logout
        /kick Bob                       kick username                       Usage: /kick
        /ban Bob 10m                    ban username duration?              Usage: /ban <username> <duration?>

        /who Bob                        who username                        Usage: /who <username>

        /name Bob Wanda                 name username username?             Usage: /name <username>
        /pass password123               pass password

     */
public class CommandParser
{
    private static HashMap<String, CommandPattern> COMMANDS = new HashMap<>();

    public static void addPattern(int permissionLevel, String pattern)
    {
        CommandPattern command = new CommandPattern(pattern, permissionLevel);
        COMMANDS.put(command.name, command);
    }

    static
    {
        try
        {
            addPattern(User.GUEST,"register string:username string:password");

            addPattern(User.GUEST,"login string:username string:password");
            addPattern(User.USER,"logout");

            addPattern(User.USER,"setpass string:old string:new");

            addPattern(User.USER, "say string:message");
            addPattern(User.USER,"tell user:user string:message");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Command parse(String input)
            throws NoLeadingSlashException, CommandNotRecognisedException, InvalidArgumentCountException
    {
        return parse(new Request(input));
    }

    public static Command parse(Request request)
            throws NoLeadingSlashException, CommandNotRecognisedException, InvalidArgumentCountException
    {
        String input = request.input;

        if (!input.startsWith("/"))
        {
            input = "/say " + input;
        }
            //throw new NoLeadingSlashException();

        int firstSpace = (input + ' ').indexOf(' ');
        String name = input.substring(1, firstSpace);

        CommandPattern pattern = COMMANDS.get(name);

        if (pattern == null)
            throw new CommandNotRecognisedException(name);

        String[] arguments = pattern.parseArguments(input);

        return new Command(request, pattern, arguments);
    }
}
